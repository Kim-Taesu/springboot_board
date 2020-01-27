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
                return "error : 비밀번호 오류";
            }
        } else {
            return "error : 일치하는 id 없음";
        }
    }

    public String signUp(Account account) {
        Optional<Account> byId = repository.findById(account.getId());
        if (byId.isPresent()) {
            return "error : 이미 id가 존재";
        } else {
            repository.save(account);
            return "success";
        }
    }
}
