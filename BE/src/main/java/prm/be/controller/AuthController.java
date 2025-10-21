package prm.be.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import prm.be.dto.request.account.LoginRequest;
import prm.be.dto.response.account.LoginResponse;
import prm.be.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Logout endpoint - thêm token vào blacklist
     * 
     * @param token JWT token cần logout
     * @return ResponseEntity với thông báo logout thành công
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody String token) {
        try {
            // Log token để debug
            log.info("Logout request received for token: {}",
                    token != null ? token.substring(0, Math.min(20, token.length())) + "..." : "null");

            // TODO: Implement blacklist logic nếu cần
            // Hiện tại chỉ log và trả về success
            // Trong tương lai có thể thêm:
            // - Lưu token vào blacklist database
            // - Invalidate token trong cache
            // - Gửi notification đến các service khác

            return ResponseEntity.ok("Logout thành công");
        } catch (Exception e) {
            log.error("Error during logout: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Lỗi khi logout: " + e.getMessage());
        }
    }
}
