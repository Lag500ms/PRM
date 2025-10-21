package com.example.myapplication.model.account.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountResponseDTO {
    private String id;
    private String username;
    private String email;
    private AccountDetails details;
}
