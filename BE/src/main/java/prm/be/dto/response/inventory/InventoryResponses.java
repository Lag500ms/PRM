package prm.be.dto.response.inventory;

import lombok.Data;

import java.util.List;

@Data
public class InventoryResponses {

    @Data
    public static class VehicleItem {
        private String vehicleId;
        private String model;
        private String version;
        private String color;
        private Integer quantity;
        private String categoryName;
    }

    @Data
    public static class InventoryResponse {
        private String id;
        private String accountId;
        private List<VehicleItem> vehicles;
    }
}
