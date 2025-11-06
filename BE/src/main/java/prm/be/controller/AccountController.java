package prm.be.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import prm.be.dto.request.account.RegisterRequestDTO;
import prm.be.dto.request.account.FullRegisterRequestDTO;
import prm.be.dto.request.account.AccountUpdateRequestDTO;
import prm.be.dto.response.account.AccountResponseDTO;
import prm.be.entity.Account;
import prm.be.service.AccountService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final ModelMapper modelMapper;

    @PostMapping("/register")
    public void register(@Valid @RequestBody RegisterRequestDTO request) {
        accountService.registerByGuest(request);
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN')")
    public void saveByAdmin(@Valid @RequestBody RegisterRequestDTO request) {
        accountService.createDealerByAdmin(request);
    }

    /**
     * Register account with full information (username, password, email, role, fullName, phone, address, isActive)
     * Public endpoint - anyone can register with full details
     */
    @PostMapping("/register-full")
    public ResponseEntity<AccountResponseDTO> registerWithFullInfo(@Valid @RequestBody FullRegisterRequestDTO request) {
        Account created = accountService.createAccountWithFullInfo(request, false);
        return ResponseEntity.ok(modelMapper.map(created, AccountResponseDTO.class));
    }

    /**
     * Create account with full information by Admin
     * Admin can create any type of account (ADMIN, DEALER) with complete information
     */
    @PostMapping("/create-full")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AccountResponseDTO> createFullAccountByAdmin(@Valid @RequestBody FullRegisterRequestDTO request) {
        Account created = accountService.createAccountWithFullInfo(request, true);
        return ResponseEntity.ok(modelMapper.map(created, AccountResponseDTO.class));
    }

    @GetMapping("/getAll")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AccountResponseDTO>> getAll() {
        return ResponseEntity.ok(
                accountService.getAll()
                        .stream()
                        .map(acc -> modelMapper.map(acc, AccountResponseDTO.class))
                        .toList()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AccountResponseDTO> getById(@PathVariable("id") String id) {
        return ResponseEntity.ok(modelMapper.map(accountService.getAccountById(id), AccountResponseDTO.class));
    }

    @GetMapping("/by-email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AccountResponseDTO> getByEmail(@PathVariable("email") String email) {
        return ResponseEntity.ok(modelMapper.map(accountService.getAccountByEmail(email), AccountResponseDTO.class));
    }

    @GetMapping("/by-username/{username}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AccountResponseDTO> getByUsername(@PathVariable("username") String username) {
        return ResponseEntity.ok(modelMapper.map(accountService.getAccountByUsername(username), AccountResponseDTO.class));
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AccountResponseDTO> update(@Valid @RequestBody AccountUpdateRequestDTO request) {
        Account updated = accountService.updateAccountById(request);
        return ResponseEntity.ok(modelMapper.map(updated, AccountResponseDTO.class));
    }

    /**
     * Public update endpoint - No authentication required
     * Anyone can update their account information
     */
    @PutMapping("/update-public")
    public ResponseEntity<AccountResponseDTO> updatePublic(@Valid @RequestBody AccountUpdateRequestDTO request) {
        Account updated = accountService.updateAccountById(request);
        return ResponseEntity.ok(modelMapper.map(updated, AccountResponseDTO.class));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        accountService.deleteAccountById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Public delete endpoint - No authentication required
     * Anyone can delete an account by ID
     */
    @DeleteMapping("/delete-public/{id}")
    public ResponseEntity<Void> deletePublic(@PathVariable String id) {
        accountService.deleteAccountById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/account/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> changeAccountStatus(
            @RequestParam String email,
            @RequestParam boolean active) {

        accountService.setAccountActive(email, active);

        Map<String, String> response = new HashMap<>();
        response.put("message", active ? "Account activated" : "Account deactivated");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AccountResponseDTO>> searchAccountsByUsername(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Account> accountPage = accountService.searchAccount(keyword, page, size);

        Page<AccountResponseDTO> response = accountPage.map(acc ->
                modelMapper.map(acc, AccountResponseDTO.class)
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Public search endpoint - No authentication required
     * Anyone can search accounts by username (useful for public user directory)
     */
    @GetMapping("/search-public")
    public ResponseEntity<Page<AccountResponseDTO>> searchAccountsPublic(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Account> accountPage = accountService.searchAccount(keyword, page, size);

        Page<AccountResponseDTO> response = accountPage.map(acc ->
                modelMapper.map(acc, AccountResponseDTO.class)
        );

        return ResponseEntity.ok(response);
    }

}
