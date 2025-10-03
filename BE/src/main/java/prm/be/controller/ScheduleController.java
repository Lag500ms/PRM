package prm.be.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import prm.be.dto.request.schedule.ScheduleRequests.CreateScheduleRequest;
import prm.be.dto.request.schedule.ScheduleRequests.UpdateScheduleRequest;
import prm.be.dto.request.schedule.ScheduleRequests.UpdateStatusRequest;
import prm.be.dto.response.schedule.ScheduleResponses.ScheduleResponse;
import prm.be.entity.Account;
import prm.be.service.AccountService;
import prm.be.service.ScheduleService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/dealer/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final AccountService accountService;

    @GetMapping
    @PreAuthorize("hasRole('DEALER')")
    public ResponseEntity<Page<ScheduleResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Account acc = accountService.getAccountByUsername(username);
        return ResponseEntity.ok(scheduleService.listByFilters(acc.getId(), status, start, end, page, size));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('DEALER')")
    public ResponseEntity<ScheduleResponse> getById(@PathVariable String id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Account acc = accountService.getAccountByUsername(username);
        return ResponseEntity.ok(scheduleService.getByIdForDealer(id, acc.getId()));
    }

    @PostMapping
    @PreAuthorize("hasRole('DEALER')")
    public ResponseEntity<ScheduleResponse> create(@Valid @RequestBody CreateScheduleRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Account acc = accountService.getAccountByUsername(username);
        return ResponseEntity.ok(scheduleService.createForDealer(request, acc.getId()));
    }

    @PutMapping
    @PreAuthorize("hasRole('DEALER')")
    public ResponseEntity<ScheduleResponse> update(@Valid @RequestBody UpdateScheduleRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Account acc = accountService.getAccountByUsername(username);
        return ResponseEntity.ok(scheduleService.updateForDealer(request, acc.getId()));
    }

    @PutMapping("/status")
    @PreAuthorize("hasRole('DEALER')")
    public ResponseEntity<ScheduleResponse> updateStatus(@Valid @RequestBody UpdateStatusRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Account acc = accountService.getAccountByUsername(username);
        return ResponseEntity.ok(scheduleService.updateStatus(request, acc.getId()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DEALER')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Account acc = accountService.getAccountByUsername(username);
        scheduleService.deleteForDealer(id, acc.getId());
        return ResponseEntity.noContent().build();
    }
}
