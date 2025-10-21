package com.example.myapplication.model.account.request;

import com.example.myapplication.model.account.response.AccountDetails;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountUpdateRequestDTO {
    private String id;
    private String username;
    private String password;
    private AccountDetails details;
}

