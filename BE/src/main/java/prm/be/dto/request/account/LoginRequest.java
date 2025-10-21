package prm.be.dto.request.account;
import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
