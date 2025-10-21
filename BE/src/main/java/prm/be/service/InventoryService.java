package prm.be.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import prm.be.dto.request.inventory.InventoryRequests.CreateInventoryRequest;
import prm.be.dto.request.inventory.InventoryRequests.UpdateVehicleQuantityRequest;
import prm.be.dto.response.inventory.InventoryResponses.InventoryResponse;
import prm.be.dto.response.inventory.InventoryResponses.VehicleItem;
import prm.be.entity.*;
import prm.be.exception.NotFoundException;
import prm.be.repository.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final VehicleInventoryRepository vehicleInventoryRepository;
    private final VehicleRepository vehicleRepository;
    private final AccountRepository accountRepository;

    public List<InventoryResponse> getInventoriesByDealer(String accountId) {
        // list inventories owned by the current dealer
        return inventoryRepository.findByAccount_Id(accountId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public InventoryResponse getInventoryByIdForDealer(String inventoryId, String accountId) {
        // get a single inventory owned by the current dealer
        Inventory inv = inventoryRepository.findByIdAndAccount_Id(inventoryId, accountId)
                .orElseThrow(() -> new NotFoundException("Inventory not found"));
        return toResponse(inv);
    }

    @Transactional
    public InventoryResponse createInventoryForDealer(CreateInventoryRequest request, String accountId) {
        // create inventory for the current dealer
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found"));
        Inventory inv = Inventory.builder().account(account).build();
        inv = inventoryRepository.save(inv);
        return toResponse(inv);
    }

    @Transactional
    public InventoryResponse updateVehicleQuantity(UpdateVehicleQuantityRequest request, String accountId) {
        // Nhận xe từ vehicle và thêm vào dealer inventory
        Inventory inv = inventoryRepository.findByIdAndAccount_Id(request.getInventoryId(), accountId)
                .orElseThrow(() -> new NotFoundException("Inventory not found"));

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new NotFoundException("Vehicle not found"));

        // Kiểm tra đủ quantity từ Vehicle
        if (vehicle.getQuantity() < request.getQuantity()) {
            throw new RuntimeException(
                    "Không đủ số lượng vehicle. Hiện có: " + vehicle.getQuantity() +
                            ", cần nhận: " + request.getQuantity());
        }

        // Trừ quantity từ Vehicle
        int newVehicleQuantity = vehicle.getQuantity() - request.getQuantity();
        vehicle.setQuantity(newVehicleQuantity);
        vehicleRepository.save(vehicle);

        // Tìm hoặc tạo VehicleInventory cho dealer
        VehicleInventory vi = vehicleInventoryRepository
                .findByInventory_Id(inv.getId())
                .stream()
                .filter(x -> x.getVehicle().getId().equals(vehicle.getId()))
                .findFirst()
                .orElse(VehicleInventory.builder().inventory(inv).vehicle(vehicle).quantity(0).build());

        // Cộng thêm quantity vào dealer inventory
        int newDealerQuantity = vi.getQuantity() + request.getQuantity();
        vi.setQuantity(newDealerQuantity);
        vehicleInventoryRepository.save(vi);

        return toResponse(inv);
    }

    @Transactional
    public void deleteInventoryForDealer(String inventoryId, String accountId) {
        // delete a dealer-owned inventory
        Inventory inv = inventoryRepository.findByIdAndAccount_Id(inventoryId, accountId)
                .orElseThrow(() -> new NotFoundException("Inventory not found"));
        inventoryRepository.delete(inv);
    }

    private InventoryResponse toResponse(Inventory inv) {
        InventoryResponse resp = new InventoryResponse();
        resp.setId(inv.getId());
        resp.setAccountId(inv.getAccount().getId());
        List<VehicleItem> items = vehicleInventoryRepository.findByInventory_Id(inv.getId())
                .stream()
                .map(vi -> {
                    VehicleItem item = new VehicleItem();
                    item.setVehicleId(vi.getVehicle().getId());
                    item.setModel(vi.getVehicle().getModel());
                    item.setVersion(vi.getVehicle().getVersion());
                    item.setColor(vi.getVehicle().getColor());
                    item.setQuantity(vi.getQuantity());
                    item.setCategoryName(vi.getVehicle().getCategory().getName());
                    return item;
                })
                .collect(Collectors.toList());
        resp.setVehicles(items);
        return resp;
    }
}
