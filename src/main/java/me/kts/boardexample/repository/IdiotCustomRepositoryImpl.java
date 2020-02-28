package me.kts.boardexample.repository;

import me.kts.boardexample.domain.Board;
import me.kts.boardexample.domain.Comment;
import me.kts.boardexample.domain.Idiot;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class IdiotCustomRepositoryImpl implements IdiotCustomRepository {
    private final MongoTemplate mongoTemplate;

    public IdiotCustomRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    @Override
    public List<Board> findExistBoard(String username, String type, String typeId, String idiotId) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("idiotType").is(type)),
                Aggregation.match(Criteria.where("idiotDetail._id").is(typeId))
        );
        return mongoTemplate.aggregate(aggregation, Idiot.class, Board.class).getMappedResults();
    }

    @Override
    public List<Comment> findExistComment(String username, String type, String typeId, String idiotId) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("idiotType").is(type)),
                Aggregation.match(Criteria.where("idiotDetail._id").is(typeId))
        );
        return mongoTemplate.aggregate(aggregation, Idiot.class, Comment.class).getMappedResults();
    }
}
