package me.kts.boardexample.service;

import me.kts.boardexample.domain.Account;
import me.kts.boardexample.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository repository;

    public AccountService(AccountRepository repository) {
        this.repository = repository;
    }

    public Object login(String id, String password) {
        Optional<Account> byId = repository.findById(id);
        if (byId.isPresent()) {
            Account account = byId.get();
            if (account.getPassword().equals(password)) {
                return account;
            } else {
                return "err(비밀번호 오류)";
            }
        } else {
            return "err(일치하는 id 없음)";
        }
    }

    public String signUp(String id, String password, String name) {
        Optional<Account> byId = repository.findById(id);
        if (byId.isPresent()) {
            return "err(이미 id가 존재)";
        } else {
            repository.save(new Account(id, password, name));
            return "success";
        }
    }
}
