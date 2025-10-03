package com.example.myapplication.model.orders;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class CreateOrderRequest {
    @SerializedName("customer")
    public String customer;
    @SerializedName("phone")
    public String phone;
    @SerializedName("address")
    public String address;
    @SerializedName("totalPrice")
    public BigDecimal totalPrice;
}
