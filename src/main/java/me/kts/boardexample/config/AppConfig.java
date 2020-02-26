package me.kts.boardexample.config;

import lombok.extern.slf4j.Slf4j;
import me.kts.boardexample.repository.AccountRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
public class AppConfig implements ApplicationRunner {

    private final AccountRepository repository;

    public AppConfig(AccountRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("init database");
//        repository.deleteAll();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
