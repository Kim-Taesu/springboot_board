package me.kts.boardexample.service;

import me.kts.boardexample.domain.Comment;
import me.kts.boardexample.domain.CommentDto;
import me.kts.boardexample.repository.BoardRepository;
import me.kts.boardexample.repository.CommentRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    public CommentService(CommentRepository commentRepository, BoardRepository boardRepository) {
        this.commentRepository = commentRepository;
        this.boardRepository = boardRepository;
    }

    public boolean createComment(CommentDto commentDto) {
        String userId = getUserId();
        Comment comment = Comment.builder()
                .boardId(commentDto.getBoardId())
                .content(commentDto.getComment())
                .build();
        comment.makeId(userId);
        commentRepository.save(comment);
        return true;
    }

    private String getUserId() {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getUsername();
    }

    public boolean updateComment(String commentId, CommentDto commentDto) {
        Comment comment = getComment(commentId);
        if (comment != null) {
            comment.setContent(commentDto.getComment());
            comment.setPersisted(true);
            commentRepository.save(comment);
            return true;
        }
        return false;
    }

    public boolean deleteComment(String commentId) {
        Comment comment = getComment(commentId);
        if (comment != null) {
            commentRepository.deleteById(commentId);
            return true;
        }

        return false;
    }


    private Comment getComment(String commentId) {
        return commentRepository.findById(commentId).orElse(null);
    }

    private boolean isBoardExists(String boardId) {
        return boardRepository.findById(boardId).isPresent();
    }

    public List<Comment> getComments(String boardId) {
        return commentRepository.findAllByBoardId(boardId);
    }
}
