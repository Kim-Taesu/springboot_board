package me.kts.boardexample.repository;

import me.kts.boardexample.domain.Account;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AccountRepository extends MongoRepository<Account, String> {
    List<Account> findByIdiotCountGreaterThan(int idiotCount);
}
