package prm.be.dto.request.inventory;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InventoryRequests {

    @Data
    public static class CreateInventoryRequest {
        @NotBlank
        private String accountId; // Optional: if creating explicitly, else inferred from auth
    }

    @Data
    public static class UpdateVehicleQuantityRequest {
        @NotBlank
        private String inventoryId;

        @NotBlank
        private String vehicleId;

        @NotNull
        @Min(0)
        private Integer quantity;
    }
}
