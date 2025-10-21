package prm.be.dto.request.vehicle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleRequestDTO {
    private String categoryId;
    private String accountId;
    private String name;
    private String color;
    private BigDecimal price;
    private String model;
    private String version;
    private String image;
    private Integer quantity;
}
