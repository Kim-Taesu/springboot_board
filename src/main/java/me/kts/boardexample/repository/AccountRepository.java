package me.kts.boardexample.repository;

import me.kts.boardexample.domain.Account;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AccountRepository extends MongoRepository<Account, String> {
}
