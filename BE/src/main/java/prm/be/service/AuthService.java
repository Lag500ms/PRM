package prm.be.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import prm.be.dto.request.LoginRequest;
import prm.be.dto.response.LoginResponse;
import prm.be.exception.AccountNotActiveException;
import prm.be.exception.UnauthorizedException;
import prm.be.jwt.JwtUtil;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            String username = authentication.getName();
            String role = authentication.getAuthorities().iterator().next().getAuthority();

            String token = jwtUtil.generateToken(username, role);

            return LoginResponse.builder()
                    .token(token)
                    .username(username)
                    .role(role)
                    .build();

        } catch (DisabledException e) {
            throw new AccountNotActiveException("Account is not active");
        } catch (BadCredentialsException e) {
            throw new UnauthorizedException("Invalid username or password");
        }
    }
}
