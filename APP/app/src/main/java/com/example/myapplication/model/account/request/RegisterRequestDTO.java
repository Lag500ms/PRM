package com.example.myapplication.model.account.request;

public class RegisterRequestDTO {
    private String username;
    private String password;
    private String email;
    private Boolean createdByAdmin;

    public RegisterRequestDTO() {}
    public RegisterRequestDTO(String username, String password, String email, Boolean createdByAdmin) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.createdByAdmin = createdByAdmin;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Boolean getCreatedByAdmin() { return createdByAdmin; }
    public void setCreatedByAdmin(Boolean createdByAdmin) { this.createdByAdmin = createdByAdmin; }
}

