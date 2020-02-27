package me.kts.boardexample.repository;

import me.kts.boardexample.domain.AccountVisit;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AccountVisitRepository extends MongoRepository<AccountVisit, String> {
}
