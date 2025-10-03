package com.example.myapplication.model.dashboard;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class RecentOrderItem {
    @SerializedName("id")
    public String id;
    @SerializedName("customer")
    public String customer;
    @SerializedName("totalPrice")
    public BigDecimal totalPrice;
    @SerializedName("status")
    public String status;
    @SerializedName("createdAt")
    public String createdAt;
}
