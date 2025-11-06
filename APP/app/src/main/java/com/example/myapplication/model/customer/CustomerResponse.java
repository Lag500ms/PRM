package com.example.myapplication.model.customer;

import com.google.gson.annotations.SerializedName;

public class CustomerResponse {
    @SerializedName("id")
    public String id;

    @SerializedName("username")
    public String username;

    @SerializedName("email")
    public String email;

    @SerializedName("fullName")
    public String fullName;

    @SerializedName("phone")
    public String phone;

    @SerializedName("address")
    public String address;

    @SerializedName("createdAt")
    public String createdAt;

    @SerializedName("status")
    public String status;
}

