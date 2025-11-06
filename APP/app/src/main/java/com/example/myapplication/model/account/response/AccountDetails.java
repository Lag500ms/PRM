package com.example.myapplication.model.account.response;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDetails {
    @SerializedName("fullName")
    private String fullName;

    @SerializedName("phone")  // Backend returns "phone" not "phoneNumber"
    private String phone;

    @SerializedName("address")
    private String address;

    // Manual getters for Lombok compatibility
    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public String getPhoneNumber() {
        return phone;  // Alias for getPhone()
    }

    public String getAddress() {
        return address;
    }
}
