package prm.be.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prm.be.dto.request.vehicle.VehicleRequestDTO;
import prm.be.entity.Account;
import prm.be.entity.Category;
import prm.be.entity.Vehicle;
import prm.be.exception.NotFoundException;
import prm.be.repository.AccountRepository;
import prm.be.repository.CategoryRepository;
import prm.be.repository.VehicleRepository;

@Service
@AllArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final CategoryRepository categoryRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public Vehicle create(VehicleRequestDTO dto) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + dto.getCategoryId()));
        Account account = accountRepository.findById(dto.getAccountId())
                .orElseThrow(() -> new NotFoundException("Account not found with id: " + dto.getAccountId()));

        Vehicle vehicle = Vehicle.builder()
                .category(category)
                .account(account)
                .name(dto.getName())
                .color(dto.getColor())
                .price(dto.getPrice())
                .model(dto.getModel())
                .version(dto.getVersion())
                .image(dto.getImage())
                .quantity(dto.getQuantity())
                .build();

        return vehicleRepository.save(vehicle);
    }

    @Transactional
    public Vehicle update(String id, VehicleRequestDTO dto) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Vehicle not found with id: " + id));

        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found with id: " + dto.getCategoryId()));
            vehicle.setCategory(category);
        }

        if (dto.getAccountId() != null) {
            Account account = accountRepository.findById(dto.getAccountId())
                    .orElseThrow(() -> new NotFoundException("Account not found with id: " + dto.getAccountId()));
            vehicle.setAccount(account);
        }
        vehicle.setName(dto.getName());
        vehicle.setColor(dto.getColor());
        vehicle.setPrice(dto.getPrice());
        vehicle.setModel(dto.getModel());
        vehicle.setVersion(dto.getVersion());
        vehicle.setImage(dto.getImage());
        vehicle.setQuantity(dto.getQuantity());

        return vehicleRepository.save(vehicle);
    }

    public void delete(String id) {
        if (!vehicleRepository.existsById(id)) {
            throw new NotFoundException("Vehicle not found with id: " + id);
        }
        vehicleRepository.deleteById(id);
    }

    public Vehicle getById(String id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Vehicle not found with id: " + id));
    }

    public Page<Vehicle> getVehicles(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        if (keyword == null || keyword.trim().isEmpty()) {
            return vehicleRepository.findAll(pageable);
        }

        return vehicleRepository.findByNameContainingIgnoreCase(keyword, pageable);
    }

}
