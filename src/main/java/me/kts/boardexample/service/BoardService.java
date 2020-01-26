package me.kts.boardexample.service;

import lombok.NonNull;
import me.kts.boardexample.domain.Board;
import me.kts.boardexample.domain.Comment;
import me.kts.boardexample.repository.BoardCustomRepository;
import me.kts.boardexample.repository.BoardRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class BoardService {

    private final BoardRepository repository;
    private final BoardCustomRepository customRepository;

    public BoardService(BoardRepository repository, BoardCustomRepository customRepository) {
        this.repository = repository;
        this.customRepository = customRepository;
    }


    public List<Board> viewAll() {
        return repository.findAll();
    }

    public String create(String userId, String title, String content) {
        Board board = new Board();
        board.setId(userId + title);
        board.setCreatedBy(userId);
        board.setTitle(title);
        board.setContent(content);
        board.setCreateDate(new Date());
        Board save = repository.save(board);
        return save.toString();
    }

    public String delete(@NonNull String userId, String id) {
        Optional<Board> byId = repository.findById(id);
        if (byId.isPresent()) {
            Board board = byId.get();
            if (board.getCreatedBy().equals(userId)) {
                repository.deleteById(id);
                return "delete success";
            } else {
                return "삭제권한 없음";
            }
        } else {
            return "delete err";
        }
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
                return "update success";
            } else {
                return "update 권한이 없습니다.";
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

    public String createComment(String userId, String id, String content) {
        Optional<Board> byId = repository.findById(id);
        if (byId.isPresent()) {
            Board board = byId.get();
            Comment comment = new Comment();
            comment.setId(userId + content);
            comment.setUserId(userId);
            comment.setContent(content);
            board.getComments().add(comment);
            repository.save(board);
            return "success";
        } else {
            return "create comment err";
        }
    }

    public String updateComment(String userId, String commentId, String boardId, String newContent) {
        Optional<Board> byId = repository.findById(boardId);
        if (byId.isPresent()) {
            Board board = byId.get();
            List<Comment> comments = board.getComments();
            for (Comment comment : comments) {
                if (comment.getId().equals(commentId)) {
                    if (comment.getId().equals(userId + comment.getContent())) {
                        comment.setId(userId + newContent);
                        comment.setContent(newContent);
                        repository.save(board);
                        return "update comment";
                    } else {
                        return "update comment 권한 없음";
                    }
                }
            }
        }
        return "database err";
    }

    public String deleteComment(String userId, String boardId, String commentId) {
        Optional<Board> byId = repository.findById(boardId);
        String result = "";
        if (byId.isPresent()) {
            Board board = byId.get();
            List<Comment> comments = board.getComments();
            for (Comment comment : comments) {
                if (comment.getId().equals(commentId)) {
                    if (comment.getUserId().equals(userId)) {
                        customRepository.deleteComment(userId, boardId, commentId);
                        result = "delete comment success";
                        break;
                    } else {
                        result = "delete comment 권한 없음";
                        break;
                    }
                }
            }
        } else {
            result = "delete comment err";
        }
        return result;
    }
}
