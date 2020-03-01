package me.kts.boardexample.resource;

import me.kts.boardexample.controller.BoardRestController;
import me.kts.boardexample.domain.Board;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class BoardResource extends EntityModel<Board> {
    public BoardResource(Board board, Link... links) {
        super(board, links);
        add(linkTo(BoardRestController.class).slash("detail").slash(board.getBoardId()).withSelfRel());
    }
}
