package com.example.myapplication.model.dashboard;

import com.google.gson.annotations.SerializedName;

public class InventoryByCategoryItem {
    @SerializedName("categoryName")
    public String categoryName;
    @SerializedName("quantity")
    public long quantity;
}
