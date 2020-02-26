package me.kts.boardexample.service;

import me.kts.boardexample.domain.Board;
import me.kts.boardexample.domain.BoardDto;
import me.kts.boardexample.domain.Comment;
import me.kts.boardexample.repository.BoardCustomRepository;
import me.kts.boardexample.repository.BoardRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class BoardService {

    private final BoardRepository repository;
    private final BoardCustomRepository customRepository;
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy. MM. dd a HH:mm");

    public BoardService(BoardRepository repository, BoardCustomRepository customRepository) {
        this.repository = repository;
        this.customRepository = customRepository;
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
                    .createdBy(userId)
                    .lastModifiedBy(userId)
                    .boardId(String.valueOf(count))
                    .build();
            repository.save(board);
            return true;
        }
    }

    public boolean delete(String boardId) {
        String userId = getUserId();
        Optional<Board> byId = repository.findById(boardId);
        if (byId.isPresent()) {
            Board board = byId.get();
            if (board.getCreatedBy().equals(userId)) {
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
                board.setLastModifiedBy(userId);
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

    public boolean createComment(String id, String content) {
        String userId = getUserId();
        Optional<Board> byId = repository.findById(id);
        if (byId.isPresent()) {
            Board board = byId.get();

            Comment comment = Comment.builder()
                    .commentId(board.getBoardId() + userId + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .userId(userId)
                    .content(content)
                    .createdBy(userId)
                    .lastModifiedBy(userId)
                    .createDate(getTime())
                    .lastModifiedDate(getTime())
                    .build();
            board.addComment(comment);
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
            List<Comment> comments = board.getComments();
            for (Comment comment : comments) {
                if (comment.getCommentId().equals(commentId)) {
                    if (comment.getCommentId().equals(userId + comment.getContent())) {
                        comment.setCommentId(userId + newContent);
                        comment.setContent(newContent);
                        comment.setLastModifiedBy(userId);
                        comment.setLastModifiedDate(getTime());
                        board.setPersisted(true);
                        repository.save(board);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean deleteComment(String boardId, String commentId) {
        String userId = getUserId();
        Optional<Board> byId = repository.findById(boardId);
        if (byId.isPresent()) {
            Board board = byId.get();
            List<Comment> comments = board.getComments();
            for (Comment comment : comments) {
                System.out.println(comment.getCommentId() + " : " + commentId);
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
