package prm.be.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import prm.be.dto.request.order.OrderRequests.CreateOrderRequest;
import prm.be.dto.request.order.OrderRequests.UpdateOrderRequest;
import prm.be.dto.request.order.OrderRequests.UpdateStatusRequest;
import prm.be.dto.response.order.OrderResponses.OrderResponse;
import prm.be.entity.Account;
import prm.be.service.AccountService;
import prm.be.service.OrderService;

@RestController
@RequestMapping("/api/v1/dealer/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final AccountService accountService;

    @GetMapping
    @PreAuthorize("hasRole('DEALER')")
    public ResponseEntity<Page<OrderResponse>> list(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "status", required = false) String status) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Account acc = accountService.getAccountByUsername(username);
        if (status != null && !status.isBlank()) {
            return ResponseEntity.ok(orderService.listByStatus(acc.getId(), status, page, size));
        }
        return ResponseEntity.ok(orderService.listForDealer(acc.getId(), page, size));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('DEALER')")
    public ResponseEntity<OrderResponse> getById(@PathVariable String id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Account acc = accountService.getAccountByUsername(username);
        return ResponseEntity.ok(orderService.getByIdForDealer(id, acc.getId()));
    }

    @PostMapping
    @PreAuthorize("hasRole('DEALER')")
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Account acc = accountService.getAccountByUsername(username);
        return ResponseEntity.ok(orderService.createForDealer(request, acc.getId()));
    }

    @PutMapping
    @PreAuthorize("hasRole('DEALER')")
    public ResponseEntity<OrderResponse> update(@Valid @RequestBody UpdateOrderRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Account acc = accountService.getAccountByUsername(username);
        return ResponseEntity.ok(orderService.updateForDealer(request, acc.getId()));
    }

    @PutMapping("/status")
    @PreAuthorize("hasRole('DEALER')")
    public ResponseEntity<OrderResponse> updateStatus(@Valid @RequestBody UpdateStatusRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Account acc = accountService.getAccountByUsername(username);
        return ResponseEntity.ok(orderService.updateStatus(request, acc.getId()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DEALER')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Account acc = accountService.getAccountByUsername(username);
        orderService.deleteForDealer(id, acc.getId());
        return ResponseEntity.noContent().build();
    }
}
