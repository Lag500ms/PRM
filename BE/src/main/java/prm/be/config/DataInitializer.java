package prm.be.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import prm.be.entity.Account;
import prm.be.entity.Category;
import prm.be.enums.Role;
import prm.be.repository.AccountRepository;
import prm.be.repository.CategoryRepository;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {

        // --- Seed admin account ---
        if (accountRepository.findByUsername("admin123").isEmpty()) {
            Account admin = Account.builder()
                    .username("admin123")
                    .password(passwordEncoder.encode("admin123"))
                    .email("admin@example.com")
                    .role(Role.ADMIN)
                    .isActive(true)
                    .build();
            accountRepository.save(admin);
            System.out.println("Admin account created: admin123 / admin123");
        }

        // --- Seed dealer account ---
        if (accountRepository.findByUsername("dealer36").isEmpty()) {
            Account dealer = Account.builder()
                    .username("dealer36")
                    .password(passwordEncoder.encode("dealer123"))
                    .email("dealer@example.com")
                    .role(Role.DEALER)
                    .isActive(true)
                    .build();
            accountRepository.save(dealer);
            System.out.println("Dealer account created: dealer36 / dealer123");
        }

        // --- Seed sample categories ---
        if (categoryRepository.count() == 0) {
            Category c1 = Category.builder().name("Orchids").build();
            Category c2 = Category.builder().name("Succulents").build();
            Category c3 = Category.builder().name("Cactus").build();

            categoryRepository.save(c1);
            categoryRepository.save(c2);
            categoryRepository.save(c3);

            System.out.println("Sample categories created: Orchids, Succulents, Cactus");
        }
    }
}
