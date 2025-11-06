package com.example.myapplication.model.inventory;

import com.google.gson.annotations.SerializedName;

/**
 * Request để trả xe về kho Admin từ dealer inventory
 */
public class ReturnVehicleToAdminRequest {
    @SerializedName("inventoryId")
    public String inventoryId;

    @SerializedName("vehicleId")
    public String vehicleId;

    @SerializedName("quantity")
    public Integer quantity;

    public ReturnVehicleToAdminRequest() {
    }

    public ReturnVehicleToAdminRequest(String inventoryId, String vehicleId, Integer quantity) {
        this.inventoryId = inventoryId;
        this.vehicleId = vehicleId;
        this.quantity = quantity;
    }
}
