package me.kts.boardexample.repository;

import me.kts.boardexample.domain.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CommentRepository extends MongoRepository<Comment, String> {
    List<Comment> findAllByBoardId(String boardId);
}
