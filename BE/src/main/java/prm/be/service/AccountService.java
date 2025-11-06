package prm.be.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import prm.be.entity.Account;
import prm.be.entity.AccountDetails;
import prm.be.enums.Role;
import prm.be.exception.NotFoundException;
import prm.be.exception.UnauthorizedException;
import prm.be.repository.AccountRepository;
import prm.be.dto.request.account.RegisterRequestDTO;
import prm.be.dto.request.account.FullRegisterRequestDTO;
import prm.be.dto.request.account.AccountUpdateRequestDTO;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;


    public Account registerByGuest(RegisterRequestDTO request) {
        if (accountRepository.existsByUsername(request.getUsername())) {
            throw new UnauthorizedException("Username already exists");
        }
        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new UnauthorizedException("Email already exists");
        }

        Account toSave = Account.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .email(request.getEmail())
                .role(null)
                .isActive(false)
                .build();

        return accountRepository.save(toSave);
    }


    public Account createDealerByAdmin(RegisterRequestDTO request) {
        if (accountRepository.existsByUsername(request.getUsername())) {
            throw new UnauthorizedException("Username already exists");
        }
        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new UnauthorizedException("Email already exists");
        }

        Account toSave = Account.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .email(request.getEmail())
                .role(Role.DEALER)
                .isActive(true)
                .build();

        return accountRepository.save(toSave);
    }


    /**
     * Create account with full information (username, password, email, role, details, isActive)
     * Can be used by Admin to create any type of account with complete information
     */
    public Account createAccountWithFullInfo(FullRegisterRequestDTO request, boolean createdByAdmin) {
        // Validate username uniqueness
        if (accountRepository.existsByUsername(request.getUsername())) {
            throw new UnauthorizedException("Username already exists");
        }

        // Validate email uniqueness
        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new UnauthorizedException("Email already exists");
        }

        // Build AccountDetails if any detail field is provided
        AccountDetails details = null;
        if (StringUtils.hasText(request.getFullName()) ||
            StringUtils.hasText(request.getPhone()) ||
            StringUtils.hasText(request.getAddress())) {

            details = AccountDetails.builder()
                    .fullName(request.getFullName())
                    .phone(request.getPhone())
                    .address(request.getAddress())
                    .build();

            // Debug logging
            System.out.println("AccountDetails created: fullName=" + details.getFullName()
                + ", phone=" + details.getPhone()
                + ", address=" + details.getAddress());
        } else {
            System.out.println("No AccountDetails created - all fields are empty");
        }

        // Determine role and active status based on creator
        Role accountRole;
        boolean isActive;

        if (createdByAdmin) {
            // Admin can set any role (or default to DEALER) and any active status
            accountRole = request.getRole() != null ? request.getRole() : Role.DEALER;
            isActive = request.getIsActive() != null ? request.getIsActive() : true;
        } else {
            // Public registration: FORCE role=null and isActive=false (ignore request values)
            accountRole = null;
            isActive = false;
        }

        // Build and save account
        Account toSave = Account.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .email(request.getEmail())
                .role(accountRole)
                .details(details)
                .isActive(isActive)
                .build();

        // Debug logging before save
        System.out.println("Account to save - username: " + toSave.getUsername()
            + ", details: " + (toSave.getDetails() != null ? toSave.getDetails().getPhone() : "null"));

        Account saved = accountRepository.save(toSave);

        // Debug logging after save
        System.out.println("Account saved - id: " + saved.getId()
            + ", details: " + (saved.getDetails() != null ? saved.getDetails().getPhone() : "null"));

        return saved;
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

    @Transactional
    public Account updateAccountById(AccountUpdateRequestDTO request) {
        Account toUpdate = findAccountById(request.getId());

        if (request.getDetails() != null) {
            AccountDetails updatedDetails = updateAccountDetails(toUpdate.getDetails(), request.getDetails());
            toUpdate.setDetails(updatedDetails);
        }

        if (StringUtils.hasText(request.getUsername()) &&
                !request.getUsername().equals(toUpdate.getUsername()) &&
                accountRepository.existsByUsername(request.getUsername())) {
            throw new UnauthorizedException("Username already exists");
        }

        if (StringUtils.hasText(request.getEmail()) &&
                !request.getEmail().equals(toUpdate.getEmail()) &&
                accountRepository.existsByEmail(request.getEmail())) {
            throw new UnauthorizedException("Email already exists");
        }

        modelMapper.map(request, toUpdate);

        if (StringUtils.hasText(request.getPassword())) {
            toUpdate.setPassword(request.getPassword());
        }

        return accountRepository.save(toUpdate);
    }


    public void deleteAccountById(String id) {
        Account account = findAccountById(id);
        if (account.getRole() == Role.ADMIN) {
            throw new UnauthorizedException("Cannot delete admin account");
        }
        accountRepository.delete(account);
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

    public void setAccountActive(String email, boolean active) {
        Account account = findAccountByEmail(email);

        if (account.getRole() == Role.ADMIN) {
            throw new UnauthorizedException("Cannot change status of admin account");
        }

        account.setActive(active);
        accountRepository.save(account);
    }

    public Page<Account> searchAccount(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("username").ascending());

        if (keyword == null || keyword.trim().isEmpty()) {
            return accountRepository.findAll(pageable);
        }

        return accountRepository.findByUsernameContainingIgnoreCase(keyword, pageable);
    }

}
