package prm.be.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import prm.be.dto.request.vehicle.VehicleRequestDTO;
import prm.be.dto.response.vehicle.VehicleResponseDTO;
import prm.be.entity.Vehicle;
import prm.be.service.VehicleService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VehicleResponseDTO> create(@RequestBody VehicleRequestDTO dto) {
        Vehicle created = vehicleService.create(dto);
        return ResponseEntity.ok(VehicleResponseDTO.fromEntity(created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VehicleResponseDTO> update(
            @PathVariable("id") String id,
            @RequestBody VehicleRequestDTO dto) {
        Vehicle updated = vehicleService.update(id, dto);
        return ResponseEntity.ok(VehicleResponseDTO.fromEntity(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        vehicleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Lấy vehicle theo ID - ADMIN và DEALER đều có thể xem
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEALER')")
    public ResponseEntity<VehicleResponseDTO> getById(@PathVariable("id") String id) {
        Vehicle vehicle = vehicleService.getById(id);
        return ResponseEntity.ok(VehicleResponseDTO.fromEntity(vehicle));
    }

    /**
     * Lấy danh sách vehicles - ADMIN và DEALER đều có thể xem
     * DEALER cần xem để thêm vào inventory
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEALER')")
    public ResponseEntity<Page<VehicleResponseDTO>> getVehicles(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        Page<Vehicle> vehiclesPage = vehicleService.getVehicles(keyword, page, size);

        List<VehicleResponseDTO> dtos = vehiclesPage.getContent()
                .stream()
                .map(VehicleResponseDTO::fromEntity)
                .collect(Collectors.toList());

        Page<VehicleResponseDTO> responsePage = new PageImpl<>(
                dtos,
                vehiclesPage.getPageable(),
                vehiclesPage.getTotalElements());

        return ResponseEntity.ok(responsePage);
    }

}
