package me.kts.boardexample.config;

import lombok.extern.slf4j.Slf4j;
import me.kts.boardexample.domain.Account;
import me.kts.boardexample.repository.AccountRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Slf4j
@Configuration
public class AppConfig implements ApplicationRunner {

    private final AccountRepository repository;
    private final PasswordEncoder passwordEncoder;

    public AppConfig(AccountRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("init database");
        Optional<Account> byId = repository.findById("admin");
        if (byId.isEmpty()) {
            Account account = Account.builder()
                    .id("admin")
                    .password("admin")
                    .eMail("admin@admin")
                    .age(26)
                    .name("관리자")
                    .role("ADMIN").build();
            account.encodePassword(passwordEncoder);
            repository.save(account);
        }
    }


}
