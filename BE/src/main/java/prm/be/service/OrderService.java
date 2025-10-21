package prm.be.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import prm.be.dto.request.order.OrderRequests.CreateOrderRequest;
import prm.be.dto.request.order.OrderRequests.UpdateOrderRequest;
import prm.be.dto.request.order.OrderRequests.UpdateStatusRequest;
import prm.be.dto.response.order.OrderResponses.OrderResponse;
import prm.be.entity.Account;
import prm.be.entity.CustomerInfo;
import prm.be.entity.Order;
import prm.be.enums.OrderStatus;
import prm.be.repository.AccountRepository;
import prm.be.repository.OrderRepository;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final AccountRepository accountRepository;

    public Page<OrderResponse> listForDealer(String accountId, int page, int size) {
        // list orders for the current dealer with pagination
        Pageable pageable = PageRequest.of(page, size);
        return orderRepository.findByAccount_Id(accountId, pageable).map(this::toResponse);
    }

    public Page<OrderResponse> listByStatus(String accountId, String status, int page, int size) {
        // list orders filtered by status for the current dealer
        Pageable pageable = PageRequest.of(page, size);
        OrderStatus st = OrderStatus.valueOf(status.toUpperCase());
        return orderRepository.findByAccount_IdAndStatus(accountId, st, pageable).map(this::toResponse);
    }

    @Transactional
    public OrderResponse createForDealer(CreateOrderRequest request, String accountId) {
        // create order for dealer with customer info; default status=PENDING
        Account acc = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found"));
        Order order = Order.builder()
                .account(acc)
                .customerInfo(CustomerInfo.builder()
                        .customer(request.getCustomer())
                        .phone(request.getPhone())
                        .address(request.getAddress())
                        .build())
                .totalPrice(request.getTotalPrice())
                .status(OrderStatus.PENDING)
                .build();
        order = orderRepository.save(order);
        return toResponse(order);
    }

    public OrderResponse getByIdForDealer(String id, String accountId) {
        // get a single order owned by the current dealer
        Order order = orderRepository.findById(id)
                .filter(o -> o.getAccount().getId().equals(accountId))
                .orElseThrow(() -> new NotFoundException("Order not found"));
        return toResponse(order);
    }

    @Transactional
    public OrderResponse updateForDealer(UpdateOrderRequest request, String accountId) {
        // update customer info and total price for a dealer-owned order
        Order order = orderRepository.findById(request.getId())
                .filter(o -> o.getAccount().getId().equals(accountId))
                .orElseThrow(() -> new NotFoundException("Order not found"));
        order.getCustomerInfo().setCustomer(request.getCustomer());
        order.getCustomerInfo().setPhone(request.getPhone());
        order.getCustomerInfo().setAddress(request.getAddress());
        order.setTotalPrice(request.getTotalPrice());
        order = orderRepository.save(order);
        return toResponse(order);
    }

    @Transactional
    public OrderResponse updateStatus(UpdateStatusRequest request, String accountId) {
        // change status for a dealer-owned order
        Order order = orderRepository.findById(request.getId())
                .filter(o -> o.getAccount().getId().equals(accountId))
                .orElseThrow(() -> new NotFoundException("Order not found"));
        order.setStatus(OrderStatus.valueOf(request.getStatus().toUpperCase()));
        order = orderRepository.save(order);
        return toResponse(order);
    }

    @Transactional
    public void deleteForDealer(String id, String accountId) {
        // delete a dealer-owned order
        Order order = orderRepository.findById(id)
                .filter(o -> o.getAccount().getId().equals(accountId))
                .orElseThrow(() -> new NotFoundException("Order not found"));
        orderRepository.delete(order);
    }

    private OrderResponse toResponse(Order order) {
        OrderResponse resp = new OrderResponse();
        resp.setId(order.getId());
        resp.setCustomer(order.getCustomerInfo() != null ? order.getCustomerInfo().getCustomer() : null);
        resp.setPhone(order.getCustomerInfo() != null ? order.getCustomerInfo().getPhone() : null);
        resp.setAddress(order.getCustomerInfo() != null ? order.getCustomerInfo().getAddress() : null);
        resp.setTotalPrice(order.getTotalPrice());
        resp.setStatus(order.getStatus() != null ? order.getStatus().name() : null);
        resp.setCreatedAt(order.getCreatedAt());
        resp.setUpdatedAt(order.getUpdatedAt());
        return resp;
    }
}
