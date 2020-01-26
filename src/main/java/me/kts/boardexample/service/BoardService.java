package me.kts.boardexample.service;

import me.kts.boardexample.domain.Board;
import me.kts.boardexample.repository.BoardRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class BoardService {

    private final BoardRepository repository;

    public BoardService(BoardRepository repository) {
        this.repository = repository;
    }


    public List<Board> viewAll() {
        return repository.findAll();
    }

    public String create(String userId, String title, String content) {
        Board board = new Board();
        board.setCreatedBy(userId);
        board.setTitle(title);
        board.setContent(content);
        board.setCreateDate(new Date());
        Board save = repository.save(board);
        return save.toString();
    }

    public void delete(String id) {
        repository.deleteById(id);
    }

    public String update(String userId, String id, String title, String content) {
        Optional<Board> byId = repository.findById(id);
        if (byId.isPresent()) {
            Board board = byId.get();
            if (board.getCreatedBy().equals(userId)) {
                board.setTitle(title);
                board.setContent(content);
                board.setLastModifiedBy(userId);
                board.setLastModifiedDate(new Date());
                repository.save(board);
                return "update 완료";
            } else {
                return "권한이 없습니다.";
            }
        } else {
            return "update 오류";
        }
    }

    public Object detailBoard(String id) {
        Optional<Board> byId = repository.findById(id);
        if (byId.isPresent()) {
            return byId.get();
        } else {
            return "detail err";
        }
    }
}
