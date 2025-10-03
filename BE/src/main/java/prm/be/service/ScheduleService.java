package prm.be.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import prm.be.dto.request.schedule.ScheduleRequests.CreateScheduleRequest;
import prm.be.dto.request.schedule.ScheduleRequests.UpdateScheduleRequest;
import prm.be.dto.request.schedule.ScheduleRequests.UpdateStatusRequest;
import prm.be.dto.response.schedule.ScheduleResponses.ScheduleResponse;
import prm.be.entity.Account;
import prm.be.entity.CustomerInfo;
import prm.be.entity.Schedule;
import prm.be.enums.ScheduleStatus;
import prm.be.exception.NotFoundException;
import prm.be.repository.AccountRepository;
import prm.be.repository.ScheduleRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final AccountRepository accountRepository;

    public Page<ScheduleResponse> listForDealer(String accountId, int page, int size) {
        // list schedules for the current dealer with pagination
        Pageable pageable = PageRequest.of(page, size);
        return scheduleRepository.findByAccount_Id(accountId, pageable).map(this::toResponse);
    }

    public Page<ScheduleResponse> listByFilters(String accountId, String status, LocalDateTime start, LocalDateTime end,
            int page, int size) {
        // list schedules filtered by status or by a date range for the dealer
        Pageable pageable = PageRequest.of(page, size);
        if (status != null && !status.isBlank()) {
            ScheduleStatus st = ScheduleStatus.valueOf(status.toUpperCase());
            return scheduleRepository.findByAccount_IdAndStatus(accountId, st, pageable).map(this::toResponse);
        }
        if (start != null && end != null) {
            return scheduleRepository.findByAccount_IdAndDateTimeBetween(accountId, start, end, pageable)
                    .map(this::toResponse);
        }
        return listForDealer(accountId, page, size);
    }

    public ScheduleResponse getByIdForDealer(String id, String accountId) {
        // get a single schedule owned by the current dealer
        Schedule s = scheduleRepository.findById(id)
                .filter(x -> x.getAccount().getId().equals(accountId))
                .orElseThrow(() -> new NotFoundException("Schedule not found"));
        return toResponse(s);
    }

    @Transactional
    public ScheduleResponse createForDealer(CreateScheduleRequest request, String accountId) {
        // create schedule for dealer with customer info; default status=PENDING
        Account acc = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found"));
        Schedule s = Schedule.builder()
                .account(acc)
                .customerInfo(CustomerInfo.builder()
                        .customer(request.getCustomer())
                        .phone(request.getPhone())
                        .address(request.getAddress())
                        .build())
                .dateTime(request.getDateTime())
                .status(ScheduleStatus.PENDING)
                .build();
        s = scheduleRepository.save(s);
        return toResponse(s);
    }

    @Transactional
    public ScheduleResponse updateForDealer(UpdateScheduleRequest request, String accountId) {
        // update customer info and date/time for a dealer-owned schedule
        Schedule s = scheduleRepository.findById(request.getId())
                .filter(x -> x.getAccount().getId().equals(accountId))
                .orElseThrow(() -> new NotFoundException("Schedule not found"));
        s.getCustomerInfo().setCustomer(request.getCustomer());
        s.getCustomerInfo().setPhone(request.getPhone());
        s.getCustomerInfo().setAddress(request.getAddress());
        s.setDateTime(request.getDateTime());
        s = scheduleRepository.save(s);
        return toResponse(s);
    }

    @Transactional
    public ScheduleResponse updateStatus(UpdateStatusRequest request, String accountId) {
        // change status for a dealer-owned schedule
        Schedule s = scheduleRepository.findById(request.getId())
                .filter(x -> x.getAccount().getId().equals(accountId))
                .orElseThrow(() -> new NotFoundException("Schedule not found"));
        s.setStatus(ScheduleStatus.valueOf(request.getStatus().toUpperCase()));
        s = scheduleRepository.save(s);
        return toResponse(s);
    }

    @Transactional
    public void deleteForDealer(String id, String accountId) {
        // delete a dealer-owned schedule
        Schedule s = scheduleRepository.findById(id)
                .filter(x -> x.getAccount().getId().equals(accountId))
                .orElseThrow(() -> new NotFoundException("Schedule not found"));
        scheduleRepository.delete(s);
    }

    private ScheduleResponse toResponse(Schedule s) {
        ScheduleResponse resp = new ScheduleResponse();
        resp.setId(s.getId());
        resp.setCustomer(s.getCustomerInfo() != null ? s.getCustomerInfo().getCustomer() : null);
        resp.setPhone(s.getCustomerInfo() != null ? s.getCustomerInfo().getPhone() : null);
        resp.setAddress(s.getCustomerInfo() != null ? s.getCustomerInfo().getAddress() : null);
        resp.setDateTime(s.getDateTime());
        resp.setStatus(s.getStatus() != null ? s.getStatus().name() : null);
        resp.setCreatedAt(s.getCreatedAt());
        resp.setUpdatedAt(s.getUpdatedAt());
        return resp;
    }
}
