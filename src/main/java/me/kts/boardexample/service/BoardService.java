package me.kts.boardexample.service;

import me.kts.boardexample.domain.Board;
import me.kts.boardexample.domain.BoardDto;
import me.kts.boardexample.domain.Comment;
import me.kts.boardexample.repository.BoardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service

public class BoardService {

    private final BoardRepository repository;

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy. MM. dd a HH:mm:ss");

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

    public boolean createComment(String boardId, String content) {
        String userId = getUserId();
        Optional<Board> byId = repository.findById(boardId);
        if (byId.isPresent()) {
            Board board = byId.get();
            String localTime = getTime();
            Comment comment = Comment.builder()
                    .boardId(boardId)
                    .userId(userId)
                    .content(content)
                    .createdBy(userId)
                    .lastModifiedBy(userId)
                    .createDate(localTime)
                    .lastModifiedDate(localTime)
                    .build();
            board.addComment(userId, comment);
            board.setPersisted(true);
            repository.save(board);
            return true;
        } else {
            return false;
        }
    }

    private String getTime() {
        return simpleDateFormat.format(new Date());
    }

    private String getUserId() {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getUsername();
    }

    public boolean updateComment(String commentId, String boardId, String newContent) {
        String userId = getUserId();
        Optional<Board> byId = repository.findById(boardId);
        if (byId.isPresent()) {
            Board board = byId.get();
            Map<String, Comment> comments = board.getComments();
            Comment comment = comments.get(commentId);
            if (comment.getCreatedBy().equals(userId)) {
                comment.setContent(newContent);
                comment.setLastModifiedBy(userId);
                comment.setLastModifiedDate(getTime());
                comments.put(commentId, comment);
                board.setPersisted(true);
                repository.save(board);
                return true;
            }
        }
        return false;
    }

    public boolean deleteComment(String boardId, String commentId) {
        String userId = getUserId();
        Optional<Board> byId = repository.findById(boardId);
        if (byId.isPresent()) {
            Board board = byId.get();
            Map<String, Comment> comments = board.getComments();
            Comment comment = comments.get(commentId);
            if (comment.getCreatedBy().equals(userId) || board.getCreatedBy().equals(userId) || userId.equals("admin")) {
                comments.remove(commentId);
                board.setPersisted(true);
                repository.save(board);
                return true;
            }
        }
        return false;
    }

    public boolean addIdiotBoard(String boardId) {
        Board board = repository.findById(boardId).orElse(null);
        if (board == null) {
            return false;
        }
        board.setIdiotCount(board.getIdiotCount() + 1);
        board.setPersisted(true);
        repository.save(board);
        return true;
    }

    public Page<Board> getIdiotBoard(Pageable pageable) {
        return repository.findByIdiotCountGreaterThan(0, pageable);
    }

}
