package prm.be.dto.response.vehicle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import prm.be.entity.Vehicle;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleResponseDTO {
    private String id;
    private String categoryId;
    private String accountId;
    private String name;
    private String color;
    private BigDecimal price;
    private String model;
    private String version;
    private String image;
    private Integer quantity;

    public static VehicleResponseDTO fromEntity(Vehicle vehicle) {
        return new VehicleResponseDTO(
                vehicle.getId(),
                vehicle.getCategory().getId(),
                vehicle.getAccount().getId(),
                vehicle.getName(),
                vehicle.getColor(),
                vehicle.getPrice(),
                vehicle.getModel(),
                vehicle.getVersion(),
                vehicle.getImage(),
                vehicle.getQuantity()
        );
    }
}
