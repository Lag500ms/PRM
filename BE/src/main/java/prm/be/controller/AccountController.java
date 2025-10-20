package prm.be.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import prm.be.dto.request.RegisterRequestDTO;
import prm.be.dto.request.AccountUpdateRequestDTO;
import prm.be.dto.response.AccountDTO;
import prm.be.dto.response.AuthAccountDTO;
import prm.be.entity.Account;
import prm.be.service.AccountService;
import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final ModelMapper modelMapper;

    @PostMapping("/register")
    public ResponseEntity<AccountDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        Account saved = accountService.registerByGuest(request);
        return ResponseEntity.ok(modelMapper.map(saved, AccountDTO.class));
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AccountDTO> saveByAdmin(@Valid @RequestBody RegisterRequestDTO request) {
        Account saved = accountService.createDealerByAdmin(request);
        return ResponseEntity.ok(modelMapper.map(saved, AccountDTO.class));
    }

    @GetMapping("/getAll")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AccountDTO>> getAll() {
        return ResponseEntity.ok(
                accountService.getAll()
                        .stream()
                        .map(acc -> modelMapper.map(acc, AccountDTO.class))
                        .toList()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AccountDTO> getById(@PathVariable("id") String id) {
        return ResponseEntity.ok(modelMapper.map(accountService.getAccountById(id), AccountDTO.class));
    }

    @GetMapping("/by-email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AccountDTO> getByEmail(@PathVariable("email") String email) {
        return ResponseEntity.ok(modelMapper.map(accountService.getAccountByEmail(email), AccountDTO.class));
    }

    @GetMapping("/by-username/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthAccountDTO> getByUsername(@PathVariable("username") String username) {
        return ResponseEntity.ok(modelMapper.map(accountService.getAccountByUsername(username), AuthAccountDTO.class));
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AccountDTO> update(@Valid @RequestBody AccountUpdateRequestDTO request) {
        Account updated = accountService.updateAccountById(request);
        return ResponseEntity.ok(modelMapper.map(updated, AccountDTO.class));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        accountService.deleteAccountById(id);
        return ResponseEntity.noContent().build();
    }
}
