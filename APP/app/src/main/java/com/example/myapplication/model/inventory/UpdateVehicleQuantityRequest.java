package com.example.myapplication.model.inventory;

import com.google.gson.annotations.SerializedName;

public class UpdateVehicleQuantityRequest {
    @SerializedName("inventoryId")
    public String inventoryId;
    @SerializedName("vehicleId")
    public String vehicleId;
    @SerializedName("quantity")
    public Integer quantity;
}
