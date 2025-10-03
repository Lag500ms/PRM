package com.example.myapplication.model.dashboard;

import com.google.gson.annotations.SerializedName;

public class LowStockItem {
    @SerializedName("vehicleId")
    public String vehicleId;
    @SerializedName("model")
    public String model;
    @SerializedName("version")
    public String version;
    @SerializedName("quantity")
    public int quantity;
}
