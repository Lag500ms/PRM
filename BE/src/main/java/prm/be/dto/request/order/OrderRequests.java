package prm.be.dto.request.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderRequests {

    @Data
    public static class CreateOrderRequest {
        @NotBlank
        private String customer;
        @NotBlank
        private String phone;
        @NotBlank
        private String address;

        @NotNull
        @Min(0)
        private BigDecimal totalPrice;
    }

    @Data
    public static class UpdateOrderRequest {
        @NotBlank
        private String id;
        @NotBlank
        private String customer;
        @NotBlank
        private String phone;
        @NotBlank
        private String address;

        @NotNull
        @Min(0)
        private BigDecimal totalPrice;
    }

    @Data
    public static class UpdateStatusRequest {
        @NotBlank
        private String id;
        @NotBlank
        private String status;
    }
}
