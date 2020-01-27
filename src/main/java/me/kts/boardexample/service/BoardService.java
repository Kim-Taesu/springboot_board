package me.kts.boardexample.service;

import lombok.NonNull;
import me.kts.boardexample.domain.Board;
import me.kts.boardexample.domain.Comment;
import me.kts.boardexample.repository.BoardCustomRepository;
import me.kts.boardexample.repository.BoardRepository;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class BoardService {

    private final BoardRepository repository;
    private final BoardCustomRepository customRepository;
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public BoardService(BoardRepository repository, BoardCustomRepository customRepository) {
        this.repository = repository;
        this.customRepository = customRepository;
    }


    public List<Board> viewAll() {
        return repository.findAll();
    }

    public String create(String userId, Board board) {
        Optional<Board> byId = repository.findById(userId + board.getTitle());
        if (byId.isPresent()) {
            return "동일한 title의 게시글이 존재합니다.";
        } else {
            Date date = new Date();
            board.setBoardId(userId + board.getTitle());
            board.setCreatedBy(userId);
            board.setLastModifiedBy(userId);

            board.setCreateDate(simpleDateFormat.format(date));
            board.setLastModifiedDate(simpleDateFormat.format(date));
            Board save = repository.save(board);
            return save.toString();
        }
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

    public String update(String userId, Board newBoard) {
        Optional<Board> byId = repository.findById(newBoard.getBoardId());
        if (byId.isPresent()) {
            Board board = byId.get();
            if (board.getCreatedBy().equals(userId)) {
                board.setTitle(newBoard.getTitle());
                board.setContent(newBoard.getContent());
                board.setLastModifiedBy(userId);
                Date date = new Date();
                board.setLastModifiedDate(simpleDateFormat.format(date));
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
            Date date = new Date();

            Comment comment = new Comment();
            comment.setCommentId(userId + content);
            comment.setUserId(userId);
            comment.setContent(content);
            comment.setCreatedBy(userId);
            comment.setLastModifiedBy(userId);
            comment.setCreateDate(simpleDateFormat.format(date));
            comment.setLastModifiedDate(simpleDateFormat.format(date));
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
                if (comment.getCommentId().equals(commentId)) {
                    if (comment.getCommentId().equals(userId + comment.getContent())) {
                        comment.setCommentId(userId + newContent);
                        comment.setContent(newContent);
                        comment.setLastModifiedBy(userId);
                        comment.setLastModifiedDate(simpleDateFormat.format(new Date()));
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
                if (comment.getCommentId().equals(commentId)) {
                    if (comment.getUserId().equals(userId) || board.getCreatedBy().equals(userId)) {
                        customRepository.deleteComment(userId, boardId, commentId, comment.getCreateDate());
                        result = "delete comment success";
                    } else {
                        result = "delete comment 권한 없음";
                    }
                    break;
                }
            }
        } else {
            result = "delete comment err";
        }
        return result;
    }
}
