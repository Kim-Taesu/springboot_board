package me.kts.boardexample.repository;

import me.kts.boardexample.domain.Idiot;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IdiotRepository extends MongoRepository<Idiot, String> {
    List<Idiot> findByIdiotId(String idiotId);
}
