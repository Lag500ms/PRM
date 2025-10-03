package com.example.myapplication.model.inventory;

import com.google.gson.annotations.SerializedName;

/**
 * UI/network model for a single vehicle row inside an inventory response.
 * Mirrors backend JSON fields; not a database entity.
 */
public class VehicleItem {
    @SerializedName("vehicleId")
    public String vehicleId;
    @SerializedName("model")
    public String model;
    @SerializedName("version")
    public String version;
    @SerializedName("color")
    public String color;
    @SerializedName("quantity")
    public Integer quantity;
    @SerializedName("categoryName")
    public String categoryName;
}
