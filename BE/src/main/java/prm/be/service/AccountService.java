package prm.be.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import prm.be.entity.Account;
import prm.be.entity.AccountDetails;
import prm.be.enums.Role;
import prm.be.exception.NotFoundException;
import prm.be.repository.AccountRepository;
import prm.be.dto.request.RegisterRequestDTO;
import prm.be.dto.request.AccountUpdateRequestDTO;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;


    public Account registerByGuest(RegisterRequestDTO request) {
        Account toSave = Account.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(Role.DEALER)
                .isActive(false)
                .build();
        return accountRepository.save(toSave);
    }

    public Account createDealerByAdmin(RegisterRequestDTO request) {
        Account toSave = Account.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(Role.DEALER)
                .isActive(true)
                .build();
        return accountRepository.save(toSave);
    }

    public List<Account> getAll() {
        return accountRepository.findAll();
    }

    public Account getAccountById(String id) {
        return findAccountById(id);
    }

    public Account getAccountByEmail(String email) {
        return findAccountByEmail(email);
    }

    public Account getAccountByUsername(String username) {
        return findAccountByUsername(username);
    }

    public Account updateAccountById(AccountUpdateRequestDTO request) {
        Account toUpdate = findAccountById(request.getId());

        if (request.getDetails() != null) {
            AccountDetails updatedDetails = updateAccountDetails(toUpdate.getDetails(), request.getDetails());
            toUpdate.setDetails(updatedDetails);
        }

        modelMapper.map(request, toUpdate);

        if (StringUtils.hasText(request.getPassword())) {
            toUpdate.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return accountRepository.save(toUpdate);
    }

    public void deleteAccountById(String id) {
        accountRepository.deleteById(id);
    }

    protected Account findAccountById(String id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Account not found with id: " + id));
    }

    protected Account findAccountByEmail(String email) {
        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Account not found with email: " + email));
    }

    protected Account findAccountByUsername(String username) {
        return accountRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Account not found with username: " + username));
    }

    private AccountDetails updateAccountDetails(AccountDetails toUpdate, AccountDetails request) {
        if (toUpdate == null) {
            toUpdate = new AccountDetails();
        }
        modelMapper.map(request, toUpdate);
        return toUpdate;
    }
}
