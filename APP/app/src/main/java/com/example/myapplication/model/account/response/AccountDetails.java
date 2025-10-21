package com.example.myapplication.model.account.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountDetails {
    private String fullName;
    private String phoneNumber;
    private String address;

}

