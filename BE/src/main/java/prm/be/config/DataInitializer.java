package prm.be.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import prm.be.entity.Account;
import prm.be.enums.Role;
import prm.be.repository.AccountRepository;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        if (accountRepository.findByUsername("admin123").isEmpty()) {
            Account admin = Account.builder()
                    .username("admin123")
                    .password(passwordEncoder.encode("admin123"))
                    .email("admin@example.com")
                    .role(Role.ADMIN)
                    .isActive(true)
                    .build();

            accountRepository.save(admin);
            System.out.println(" Admin account created: admin123 / admin123");
        }

        if (accountRepository.findByUsername("dealer36").isEmpty()) {
            Account dealer = Account.builder()
                    .username("dealer36")
                    .password(passwordEncoder.encode("dealer123"))
                    .email("dealer@example.com")
                    .role(Role.DEALER)
                    .isActive(true)
                    .build();

            accountRepository.save(dealer);
            System.out.println(" Dealer account created: dealer01 / dealer123");
        }
    }
}
