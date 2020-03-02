package me.kts.boardexample.service;

import com.mongodb.client.result.UpdateResult;
import me.kts.boardexample.domain.Board;
import me.kts.boardexample.domain.BoardDto;
import me.kts.boardexample.repository.BoardRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BoardRestService {

    private final ModelMapper modelMapper;
    private final BoardRepository boardRepository;
    private final MongoTemplate mongoTemplate;

    public BoardRestService(ModelMapper modelMapper, BoardRepository boardRepository, MongoTemplate mongoTemplate) {
        this.modelMapper = modelMapper;
        this.boardRepository = boardRepository;
        this.mongoTemplate = mongoTemplate;
    }


    public Board create(BoardDto boardDto) {
        Board board = modelMapper.map(boardDto, Board.class);
        String userId = getUserId();
        board.makeId(userId);

        return boardRepository.save(board);
    }

    private String getUserId() {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getUsername();
    }

    public Page<Board> getLists(Pageable pageable) {
        return boardRepository.findAll(pageable);
    }

    public Board update(String boardId, Board newBoard) {
        String userId = getUserId();

        Optional<Board> byId = boardRepository.findById(boardId);
        if (byId.isEmpty()) {
            return null;
        }
        Board board = byId.get();
        if (!board.getCreatedBy().equals(userId)) {
            return null;
        }

        Query query = new Query(Criteria.where("_id").is(boardId)
                .andOperator(Criteria.where("createdBy").is(userId)));
        Update update = new Update();
        update.set("title", newBoard.getTitle());
        update.set("content", newBoard.getContent());

        UpdateResult updateResult = mongoTemplate.upsert(
                query,
                update,
                Board.class);
        if (updateResult.getModifiedCount() == 0) return null;
        return mongoTemplate.findOne(new Query(Criteria.where("_id").is(boardId)), Board.class);
    }

    public Board detail(String boardId) {
        return boardRepository.findById(boardId).orElse(null);
    }
}
