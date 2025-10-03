package prm.be.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import prm.be.dto.request.inventory.InventoryRequests.CreateInventoryRequest;
import prm.be.dto.request.inventory.InventoryRequests.UpdateVehicleQuantityRequest;
import prm.be.dto.response.inventory.InventoryResponses.InventoryResponse;
import prm.be.entity.Account;
import prm.be.service.AccountService;
import prm.be.service.InventoryService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dealer/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;
    private final AccountService accountService;

    @GetMapping
    @PreAuthorize("hasRole('DEALER')")
    public ResponseEntity<List<InventoryResponse>> getMyInventories() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Account acc = accountService.getAccountByUsername(username);
        return ResponseEntity.ok(inventoryService.getInventoriesByDealer(acc.getId()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('DEALER')")
    public ResponseEntity<InventoryResponse> getById(@PathVariable("id") String id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Account acc = accountService.getAccountByUsername(username);
        return ResponseEntity.ok(inventoryService.getInventoryByIdForDealer(id, acc.getId()));
    }

    @PostMapping
    @PreAuthorize("hasRole('DEALER')")
    public ResponseEntity<InventoryResponse> create(@Valid @RequestBody CreateInventoryRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Account acc = accountService.getAccountByUsername(username);
        return ResponseEntity.ok(inventoryService.createInventoryForDealer(request, acc.getId()));
    }

    @PutMapping("/quantity")
    @PreAuthorize("hasRole('DEALER')")
    public ResponseEntity<InventoryResponse> updateQuantity(@Valid @RequestBody UpdateVehicleQuantityRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Account acc = accountService.getAccountByUsername(username);
        return ResponseEntity.ok(inventoryService.updateVehicleQuantity(request, acc.getId()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DEALER')")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Account acc = accountService.getAccountByUsername(username);
        inventoryService.deleteInventoryForDealer(id, acc.getId());
        return ResponseEntity.noContent().build();
    }
}
