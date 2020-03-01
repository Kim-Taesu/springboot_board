package me.kts.boardexample.service;

import me.kts.boardexample.domain.Board;
import me.kts.boardexample.domain.BoardDto;
import me.kts.boardexample.repository.BoardRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

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

    public boolean create(BoardDto boardDto) {
        String userId = getUserId();
        Optional<Board> byId = repository.findById(userId + boardDto.getTitle());
        if (byId.isPresent()) {
            return false;
        } else {
            long count = repository.count();
            Board board = Board.builder()
                    .title(boardDto.getTitle())
                    .content(boardDto.getContent())
                    .boardId(String.valueOf(count))
                    .build();
            board.makeId(userId);
            repository.save(board);
            return true;
        }
    }

    public boolean delete(String boardId) {
        String userId = getUserId();
        Optional<Board> byId = repository.findById(boardId);
        if (byId.isPresent()) {
            Board board = byId.get();
            if (board.getCreatedBy().equals(userId) || userId.equals("admin")) {
                repository.deleteById(boardId);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean update(BoardDto newBoard, String boardId) {
        String userId = getUserId();
        Optional<Board> byId = repository.findById(boardId);
        if (byId.isPresent()) {
            Board board = byId.get();
            System.out.println("createdBy : " + board.getCreatedBy());
            System.out.println("updatedBy : " + userId);
            if (board.getCreatedBy().equals(userId)) {
                board.setTitle(newBoard.getTitle());
                board.setContent(newBoard.getContent());
                board.setPersisted(true);
                repository.save(board);
                return true;
            }
        }
        return false;
    }

    public Board detailBoard(String id) {
        Optional<Board> byId = repository.findById(id);
        return byId.orElse(null);
    }


    private String getUserId() {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getUsername();
    }
}
