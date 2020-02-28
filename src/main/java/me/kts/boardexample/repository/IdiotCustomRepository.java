package me.kts.boardexample.repository;

import me.kts.boardexample.domain.Board;
import me.kts.boardexample.domain.Comment;

import java.util.List;

public interface IdiotCustomRepository {
    List<Board> findExistBoard(String username, String type, String typeId, String idiotId);

    List<Comment> findExistComment(String username, String type, String typeId, String idiotId);
}
