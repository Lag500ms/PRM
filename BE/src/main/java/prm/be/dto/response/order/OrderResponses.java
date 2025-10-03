package prm.be.dto.response.order;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderResponses {

    @Data
    public static class OrderResponse {
        private String id;
        private String customer;
        private String phone;
        private String address;
        private BigDecimal totalPrice;
        private String status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}
