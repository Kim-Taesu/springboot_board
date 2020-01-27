package me.kts.boardexample.repository;

public interface BoardCustomRepository {
    void deleteComment(String userId, String boardId, String commentId, String createDate);
}
