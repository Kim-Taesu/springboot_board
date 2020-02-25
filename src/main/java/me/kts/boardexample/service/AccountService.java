package me.kts.boardexample.service;

import lombok.extern.slf4j.Slf4j;
import me.kts.boardexample.domain.Account;
import me.kts.boardexample.repository.AccountRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class AccountService implements UserDetailsService {

    private final AccountRepository repository;
    private final PasswordEncoder passwordEncoder;

    public AccountService(AccountRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean signUpCheck(Account account) {
        Optional<Account> byId = repository.findById(account.getId());
        if (byId.isPresent()) {
            return false;
        } else {
            return true;
        }
    }

    public Account getInfo() {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String id = principal.getUsername();
        return repository.findById(id).orElse(null);
    }

    public boolean updateInfo(Account account) {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = principal.getUsername();
        if (!username.equals(account.getId())) {
            return false;
        }

        Account byId = repository.findById(account.getId()).orElse(null);
        if (byId != null) {
            byId.setPassword(account.getPassword());
            byId.encodePassword(passwordEncoder);
            byId.setName(account.getName());
            byId.setAge(account.getAge());
            byId.setEMail(account.getEMail());
            byId.setPersisted(true);
            repository.save(byId);
            return true;
        } else {
            return false;
        }
    }

    public boolean deleteUser(String accountId) {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal.getUsername().equals(accountId)) {
            try {
                repository.deleteById(accountId);
                SecurityContextHolder.clearContext();
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
        Account account = repository.findById(id).orElse(null);
        if (account == null) {
            throw new UsernameNotFoundException(id);
        }
        return User.builder()
                .username(account.getId())
                .password(account.getPassword())
                .roles(account.getRole())
                .build();
    }


    public boolean signUp(Account account) {
        try {
            account.setRole("USER");
            account.encodePassword(passwordEncoder);
            repository.save(account);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
