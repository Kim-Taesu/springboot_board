package me.kts.boardexample.repository;

import me.kts.boardexample.domain.Board;
import me.kts.boardexample.domain.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BoardRepository extends MongoRepository<Board, String> {
    void deleteByCommentsContains(Comment comment);
}
