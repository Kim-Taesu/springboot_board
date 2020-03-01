package me.kts.boardexample.resource;

import me.kts.boardexample.controller.BoardRestController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.validation.Errors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class ErrorsResource extends EntityModel<Errors> {
    public ErrorsResource(Errors content, Link... links) {
        super(content, links);
        add(linkTo(BoardRestController.class).slash("list").withRel("board-list"));
    }
}
