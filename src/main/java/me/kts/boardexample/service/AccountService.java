package me.kts.boardexample.service;

import me.kts.boardexample.domain.Account;
import me.kts.boardexample.repository.AccountRepository;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@Service
public class AccountService {

    private final String ACCOUNT = "id";
    private final AccountRepository repository;

    public AccountService(AccountRepository repository) {
        this.repository = repository;
    }

    public boolean loginCheck(String id,
                              String password) {
        Optional<Account> byId = repository.findById(id);
        if (byId.isPresent()) {
            Account account = byId.get();
            return account.getPassword().equals(password);
        } else {
            return false;
        }
    }

    public boolean signupcheck(Account account) {
        Optional<Account> byId = repository.findById(account.getId());
        if (byId.isPresent()) {
            return false;
        } else {
            repository.save(account);
            return true;
        }
    }

    public void logout(HttpSession session) {
        session.removeAttribute(ACCOUNT);
    }

    public Account getInfo(String id) {
        return repository.findById(id).orElse(null);
    }

    public boolean updateInfo(Account account) {
        @NotNull String id = account.getId();
        Account byId = repository.findById(id).orElse(null);
        if (byId != null) {
            byId.setPassword(account.getPassword());
            byId.setName(account.getName());
            byId.setAge(account.getAge());
            byId.setEMail(account.getEMail());
            repository.save(byId);
            return true;
        } else {
            return false;
        }
    }

    public boolean deleteUser(String accountId) {
        try {
            repository.deleteById(accountId);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
