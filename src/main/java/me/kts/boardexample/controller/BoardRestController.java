package me.kts.boardexample.controller;

import lombok.extern.slf4j.Slf4j;
import me.kts.boardexample.repository.BoardRepository;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/board/api")
public class BoardRestController {

    protected final ModelMapper modelMapper;
    private final BoardRepository repository;

    public BoardRestController(ModelMapper modelMapper, BoardRepository repository) {
        this.modelMapper = modelMapper;
        this.repository = repository;
    }

//    @PostMapping("/create")
//    public ResponseEntity create(@RequestBody @Valid BoardDto boardDto,
//                                 HttpSession session) {
//        String id = session.getAttribute("id").toString();
//        Board board = modelMapper.map(boardDto, Board.class);
//        board.setCreatedBy(id);
//        board.setLastModifiedBy(id);
//        board.makeId(id, board.getTitle());
//        repository.save(board);
//    }
}
