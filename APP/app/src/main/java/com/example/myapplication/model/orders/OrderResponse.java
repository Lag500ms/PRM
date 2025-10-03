package com.example.myapplication.model.orders;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class OrderResponse {
    @SerializedName("id")
    public String id;
    @SerializedName("customer")
    public String customer;
    @SerializedName("phone")
    public String phone;
    @SerializedName("address")
    public String address;
    @SerializedName("totalPrice")
    public BigDecimal totalPrice;
    @SerializedName("status")
    public String status;
    @SerializedName("createdAt")
    public String createdAt;
    @SerializedName("updatedAt")
    public String updatedAt;
}
