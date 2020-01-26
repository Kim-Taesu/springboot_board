package me.kts.boardexample.repository;

import me.kts.boardexample.domain.Board;
import me.kts.boardexample.domain.Comment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BoardCustomRepositoryImpl implements BoardCustomRepository {

    private final MongoTemplate mongoTemplate;

    public BoardCustomRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void deleteComment(String userId, String boardId, String commentId) {
        Query collectionQuery = new Query();
        collectionQuery.addCriteria(Criteria.where("userId").is(userId));
        collectionQuery.addCriteria(Criteria.where("_id").is(commentId));

        this.mongoTemplate.updateMulti(
                new Query(),
                new Update().pull("comments", collectionQuery),
                "board");
    }
}
