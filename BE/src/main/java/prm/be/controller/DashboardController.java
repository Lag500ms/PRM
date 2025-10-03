package prm.be.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import prm.be.dto.response.dashboard.DashboardResponses.DashboardResponse;
import prm.be.entity.Account;
import prm.be.service.AccountService;
import prm.be.service.DashboardService;

@RestController
@RequestMapping("/api/v1/dealer/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final AccountService accountService;

    @GetMapping
    @PreAuthorize("hasRole('DEALER')")
    public ResponseEntity<DashboardResponse> get() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Account acc = accountService.getAccountByUsername(username);
        return ResponseEntity.ok(dashboardService.getDashboard(acc.getId()));
    }
}
