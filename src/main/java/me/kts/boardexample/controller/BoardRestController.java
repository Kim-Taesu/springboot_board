package me.kts.boardexample.controller;

import lombok.extern.slf4j.Slf4j;
import me.kts.boardexample.Validator.BoardValidator;
import me.kts.boardexample.domain.Board;
import me.kts.boardexample.domain.BoardDto;
import me.kts.boardexample.resource.BoardResource;
import me.kts.boardexample.resource.ErrorsResource;
import me.kts.boardexample.service.BoardRestService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Slf4j
@RestController
@RequestMapping("/board/api")
public class BoardRestController {

    protected final ModelMapper modelMapper;
    private final BoardRestService boardRestService;
    private final BoardValidator boardValidator;

    public BoardRestController(ModelMapper modelMapper, BoardRestService boardRestService, BoardValidator boardValidator) {
        this.modelMapper = modelMapper;
        this.boardRestService = boardRestService;
        this.boardValidator = boardValidator;
    }

    @GetMapping("/list")
    public ResponseEntity boardList(Pageable pageable, PagedResourcesAssembler<Board> assembler) {
        Page<Board> page = boardRestService.getLists(pageable);
        var boardResources = assembler.toModel(page, e -> new BoardResource(e));
        return ResponseEntity.ok(boardResources);
    }

    @PostMapping("/create")
    public ResponseEntity create(@RequestBody @Valid BoardDto boardDto, BindingResult errors) {
        if (errors.hasErrors()) {
            return badRequest(errors);
        }
        Board board = boardRestService.create(boardDto);
        WebMvcLinkBuilder linkBuilder = linkTo(BoardRestController.class).slash("detail").slash(board.getBoardId());
        URI uri = linkBuilder.toUri();

        BoardResource boardResource = new BoardResource(board);
        boardResource.add(linkTo(BoardRestController.class).slash("list").withRel("board-list"));
        return ResponseEntity.created(uri).body(boardResource);
    }

    @PutMapping("/update/{boardId}")
    public ResponseEntity updateBoard(@RequestBody @Valid Board Board, @PathVariable String boardId, BindingResult errors) {
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        Board board = boardRestService.update(boardId, Board);
        if (board == null) {
            errors.rejectValue("createdBy", "wrongUser", "access denied");
            return badRequest(errors);
        }

        BoardResource boardResource = new BoardResource(board);
        return ResponseEntity.ok(boardResource);
    }

    @GetMapping("/detail/{boardId}")
    public ResponseEntity detailBoard(@PathVariable String boardId) {
        Board board = boardRestService.detail(boardId);
        if (board == null) {
            Link linkBuilder = linkTo(BoardRestController.class).slash("list").withRel("board-list");
            return ResponseEntity.badRequest().body(linkBuilder);
        }
        BoardResource boardResource = new BoardResource(board);
        boardResource.add(linkTo(BoardRestController.class).slash("list").withRel("board-list"));
        return ResponseEntity.ok(boardResource);
    }

    private ResponseEntity badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }
}
