package com.example.myapplication.model.account.request;

import com.google.gson.annotations.SerializedName;

public class FullRegisterRequestDTO {
    @SerializedName("username")
    private String username;

    @SerializedName("password")
    private String password;

    @SerializedName("email")
    private String email;

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("phone") // Changed from "phoneNumber" to "phone"
    private String phoneNumber;

    @SerializedName("address")
    private String address;

    @SerializedName("createdByAdmin")
    private Boolean createdByAdmin;

    public FullRegisterRequestDTO() {}

    public FullRegisterRequestDTO(String username, String password, String email,
                                  String fullName, String phoneNumber, String address,
                                  Boolean createdByAdmin) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.createdByAdmin = createdByAdmin;
    }

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Boolean getCreatedByAdmin() { return createdByAdmin; }
    public void setCreatedByAdmin(Boolean createdByAdmin) { this.createdByAdmin = createdByAdmin; }
}
