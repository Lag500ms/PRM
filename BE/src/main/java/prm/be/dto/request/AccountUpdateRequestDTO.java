package prm.be.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import prm.be.entity.AccountDetails;

@Data
public class AccountUpdateRequestDTO {
    @NotBlank(message = "Id is required")
    private String id;
    private String username;
    private String password;
    private String email;
    private AccountDetails details;
}
