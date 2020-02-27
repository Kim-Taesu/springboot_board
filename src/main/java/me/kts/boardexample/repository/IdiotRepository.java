package me.kts.boardexample.repository;

import me.kts.boardexample.domain.Idiot;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface IdiotRepository extends MongoRepository<Idiot, String> {
    Optional<Idiot> findByUserIdAndBoardIdAndIdiotId(String userId, String boardId, String idiotId);

    Optional<Idiot> findByUserIdAndCommentIdAndIdiotId(String userId, String commentId, String idiotId);

    List<Idiot> findByIdiotId(String idiotId);
}
