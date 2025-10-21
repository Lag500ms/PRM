package prm.be.dto.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import prm.be.entity.AccountDetails;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountResponseDTO {
    private String id;
    private String username;
    private String email;
    private AccountDetails details;
}
