package me.kts.boardexample.service;

import me.kts.boardexample.domain.Board;
import me.kts.boardexample.domain.BoardDto;
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

    public boolean create(String userId, BoardDto boardDto) {
        Optional<Board> byId = repository.findById(userId + boardDto.getTitle());
        if (byId.isPresent()) {
            return false;
        } else {
            Date date = new Date();
            Board board = Board.builder()
                    .title(boardDto.getTitle())
                    .content(boardDto.getContent())
                    .createdBy(userId)
                    .lastModifiedBy(userId)
                    .createDate(simpleDateFormat.format(date))
                    .lastModifiedDate(simpleDateFormat.format(date))
                    .build();
            board.makeId(board.getCreatedBy(), board.getTitle());
            repository.save(board);
            return true;
        }
    }

    public boolean delete(String userId, String id) {
        Optional<Board> byId = repository.findById(id);
        if (byId.isPresent()) {
            Board board = byId.get();
            if (board.getCreatedBy().equals(userId)) {
                repository.deleteById(id);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean update(String userId, Board newBoard) {
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
                return true;
            }
        }
        return false;
    }

    public Board detailBoard(String id) {
        Optional<Board> byId = repository.findById(id);
        return byId.orElse(null);
    }

    public boolean createComment(String userId, String id, String content) {
        Optional<Board> byId = repository.findById(id);
        if (byId.isPresent()) {
            Board board = byId.get();
            Date date = new Date();

            Comment comment = Comment.builder()
                    .commentId(userId + content)
                    .userId(userId)
                    .content(content)
                    .createdBy(userId)
                    .lastModifiedBy(userId)
                    .createDate(simpleDateFormat.format(date))
                    .lastModifiedDate(simpleDateFormat.format(date))
                    .build();

            board.addComment(comment);
            repository.save(board);
            return true;
        } else {
            return false;
        }
    }

    public boolean updateComment(String userId, String commentId, String boardId, String newContent) {
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
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean deleteComment(String userId, String boardId, String commentId) {
        Optional<Board> byId = repository.findById(boardId);
        if (byId.isPresent()) {
            Board board = byId.get();
            List<Comment> comments = board.getComments();
            for (Comment comment : comments) {
                if (comment.getCommentId().equals(commentId)) {
                    if (comment.getUserId().equals(userId) || board.getCreatedBy().equals(userId)) {
                        customRepository.deleteComment(userId, boardId, commentId, comment.getCreateDate());
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
