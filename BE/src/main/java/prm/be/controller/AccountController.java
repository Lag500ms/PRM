package prm.be.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import prm.be.dto.request.RegisterRequestDTO;
import prm.be.dto.request.AccountUpdateRequestDTO;
import prm.be.dto.response.AccountResponseDTO;
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

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
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

}
