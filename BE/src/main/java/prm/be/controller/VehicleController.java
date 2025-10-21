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
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<VehicleResponseDTO> create(@RequestBody VehicleRequestDTO dto) {
        Vehicle created = vehicleService.create(dto);
        return ResponseEntity.ok(VehicleResponseDTO.fromEntity(created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<VehicleResponseDTO> update(
            @PathVariable("id") String id,
            @RequestBody VehicleRequestDTO dto) {
        Vehicle updated = vehicleService.update(id, dto);
        return ResponseEntity.ok(VehicleResponseDTO.fromEntity(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        vehicleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<VehicleResponseDTO> getById(@PathVariable("id") String id) {
        Vehicle vehicle = vehicleService.getById(id);
        return ResponseEntity.ok(VehicleResponseDTO.fromEntity(vehicle));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
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
                vehiclesPage.getTotalElements()
        );

        return ResponseEntity.ok(responsePage);
    }

}
