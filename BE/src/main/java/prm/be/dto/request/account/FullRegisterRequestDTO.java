package prm.be.dto.request.account;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import prm.be.enums.Role;

@Data
public class FullRegisterRequestDTO {

    // Account basic info
    @NotBlank(message = "Username is required")
    @Size(min = 6, message = "Username must be at least 6 characters")
    private String username;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
        message = "Password must be at least 8 characters and contain at least one letter and one number")
    @NotNull(message = "Password is required")
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    // Role - optional, default will be set in service
    private Role role;

    // Account Details - all optional
    private String fullName;

    // Phone validation removed for flexibility
    // Original pattern: ^(\\+84|0)[0-9]{9}$ (Vietnamese phone format)
    private String phone;

    private String address;

    // Active status - optional, default will be set in service
    private Boolean isActive;
}

