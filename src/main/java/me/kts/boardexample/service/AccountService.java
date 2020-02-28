package me.kts.boardexample.service;

import lombok.extern.slf4j.Slf4j;
import me.kts.boardexample.domain.Account;
import me.kts.boardexample.domain.UserAccount;
import me.kts.boardexample.repository.AccountRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean signUpCheck(Account account) {
        return accountRepository.findById(account.getId()).orElse(null) == null;
    }

    public Account getInfo() {
        String id = getUsername();
        return accountRepository.findById(id).orElse(null);
    }

    private String getUsername() {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getUsername();
    }

    public boolean updateInfo(Account account) {
        String username = getUsername();
        if (!username.equals(account.getId())) {
            return false;
        }

        Account byId = accountRepository.findById(account.getId()).orElse(null);
        if (byId != null) {
            byId.setPassword(account.getPassword());
            byId.encodePassword(passwordEncoder);
            byId.setName(account.getName());
            byId.setAge(account.getAge());
            byId.setEMail(account.getEMail());
            byId.setPersisted(true);
            accountRepository.save(byId);
            return true;
        } else {
            return false;
        }
    }

    public boolean deleteUser(String accountId) {
        String username = getUsername();
        if (username.equals(accountId)) {
            try {
                accountRepository.deleteById(accountId);
            } catch (Exception e) {
                return false;
            }
            return true;
        } else {
            return false;
        }

    }

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        Account account = accountRepository.findById(id).orElse(null);
        if (account == null) {
            throw new UsernameNotFoundException(id);
        }
        assert account.getId() != null;
        return new UserAccount(account);
    }


    public boolean signUp(Account account) {
        try {
            account.setRole("USER");
            account.encodePassword(passwordEncoder);
            accountRepository.save(account);
        } catch (Exception e) {
            log.error(e.toString());
            return false;
        }
        return true;
    }


}
