package com.example.myapplication.model.customer;

import com.google.gson.annotations.SerializedName;

public class CreateCustomerRequest {
    @SerializedName("username")
    public String username;

    @SerializedName("password")
    public String password;

    @SerializedName("email")
    public String email;

    @SerializedName("fullName")
    public String fullName;

    @SerializedName("phone")
    public String phone;

    @SerializedName("address")
    public String address;

    @SerializedName("createdByAdmin")
    public Boolean createdByAdmin;

    public CreateCustomerRequest() {}

    public CreateCustomerRequest(String username, String password, String email,
                                String fullName, String phone, String address, Boolean createdByAdmin) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
        this.createdByAdmin = createdByAdmin;
    }
}

