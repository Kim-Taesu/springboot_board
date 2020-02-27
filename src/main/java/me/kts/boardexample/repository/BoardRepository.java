package me.kts.boardexample.repository;

import me.kts.boardexample.domain.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BoardRepository extends MongoRepository<Board, String> {
    public Page<Board> findByIdiotCountGreaterThan(int idiotCount, Pageable pageable);
}
