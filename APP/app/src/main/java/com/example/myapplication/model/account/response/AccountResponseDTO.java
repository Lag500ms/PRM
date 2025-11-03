package com.example.myapplication.model.account.response;

import com.google.gson.annotations.SerializedName;

public class AccountResponseDTO {
    private String id;
    private String username;
    private String email;
    
    // BE có thể trả về "active" hoặc "isActive" trong JSON
    // Jackson serialize boolean isActive thành "active"
    @SerializedName(value = "active", alternate = {"isActive"})
    private boolean isActive;
    
    private AccountDetails details;

    public AccountResponseDTO() {}
    public AccountResponseDTO(String id, String username, String email, boolean isActive, AccountDetails details) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.isActive = isActive;
        this.details = details;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public boolean isActive() { 
        // Đảm bảo trả về đúng giá trị (default false nếu chưa được set)
        return isActive; 
    }
    public void setActive(boolean active) { 
        isActive = active; 
    }
    public AccountDetails getDetails() { return details; }
    public void setDetails(AccountDetails details) { this.details = details; }
}
