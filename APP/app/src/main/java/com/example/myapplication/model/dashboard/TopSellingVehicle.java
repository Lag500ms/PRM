package com.example.myapplication.model.dashboard;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class TopSellingVehicle {
    @SerializedName("vehicleId")
    public String vehicleId;

    @SerializedName("model")
    public String model;

    @SerializedName("version")
    public String version;

    @SerializedName("categoryName")
    public String categoryName;

    @SerializedName("totalSold")
    public long totalSold;

    @SerializedName("totalRevenue")
    public BigDecimal totalRevenue;
}
