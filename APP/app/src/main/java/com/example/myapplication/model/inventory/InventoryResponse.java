package com.example.myapplication.model.inventory;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class InventoryResponse {
    @SerializedName("id")
    public String id;
    @SerializedName("accountId")
    public String accountId;
    @SerializedName("vehicles")
    public List<VehicleItem> vehicles;
}
