package me.kts.boardexample.config;

import lombok.extern.slf4j.Slf4j;
import me.kts.boardexample.domain.Account;
import me.kts.boardexample.repository.AccountRepository;
import me.kts.boardexample.repository.AccountVisitRepository;
import me.kts.boardexample.repository.BoardRepository;
import me.kts.boardexample.repository.IdiotRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Slf4j
@Configuration
public class AppConfig implements ApplicationRunner {

    private final AccountRepository accountRepository;
    private final BoardRepository boardRepository;
    private final IdiotRepository idiotRepository;
    private final AccountVisitRepository accountVisitRepository;
    private final PasswordEncoder passwordEncoder;

    public AppConfig(AccountRepository accountRepository, BoardRepository boardRepository, IdiotRepository idiotRepository, AccountVisitRepository accountVisitRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.boardRepository = boardRepository;
        this.idiotRepository = idiotRepository;
        this.accountVisitRepository = accountVisitRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("init database");
//        accountRepository.deleteAll();
//        boardRepository.deleteAll();
//        idiotRepository.deleteAll();
//        accountVisitRepository.deleteAll();

        log.info("add admin account");
        Optional<Account> byId = accountRepository.findById("admin");
        if (byId.isEmpty()) {
            Account account = Account.builder()
                    .id("admin")
                    .password("admin")
                    .eMail("admin@admin")
                    .age(26)
                    .name("관리자")
                    .role("ADMIN").build();
            account.encodePassword(passwordEncoder);
            accountRepository.save(account);
        }
    }


}
