package me.kts.boardexample.controller;

import lombok.extern.slf4j.Slf4j;
import me.kts.boardexample.domain.Board;
import me.kts.boardexample.domain.BoardDto;
import me.kts.boardexample.domain.Comment;
import me.kts.boardexample.service.BoardService;
import me.kts.boardexample.service.CommentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.List;

@Slf4j
@Controller
@RequestMapping(value = "/board")
public class BoardController {

    private final BoardService boardService;
    private final CommentService commentService;

    public BoardController(BoardService boardService, CommentService commentService) {
        this.boardService = boardService;
        this.commentService = commentService;
    }

    @GetMapping("/create")
    public String createPage() {
        return "board/create";
    }

    @GetMapping("/list")
    public String boardList(Model model) {
        model.addAttribute("boards", boardService.viewAll());
        return "board/list";
    }

    @GetMapping("/detail/{boardId}")
    public String detailPage(Model model,
                             @PathVariable String boardId,
                             RedirectAttributes attributes) {
        Board board = boardService.detailBoard(boardId);
        List<Comment> comments = commentService.getComments(boardId);
        if (board == null) {
            attributes.addFlashAttribute("message", "detail board error");
            return "redirect:/board/list";
        } else {
            model.addAttribute("board", board);
            model.addAttribute("comments", comments);
            return "board/detail";
        }
    }

    @PostMapping(value = "/create")
    public String create(
            RedirectAttributes attributes,
            @Valid BoardDto boardDto,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(c -> {
                attributes.addFlashAttribute("message", c.getField() + " : " + c.getDefaultMessage());
            });
            return "redirect:/board/create";
        }
        if (boardService.create(boardDto)) {
            attributes.addFlashAttribute("message", "게시글 생성 성공");
            return "redirect:/board/list";
        } else {
            attributes.addFlashAttribute("message", "해당 게시글 제목이 이미 존재");
            return "redirect:/board/create";
        }
    }

    @PostMapping("/update/{boardId}")
    public String update(RedirectAttributes attributes,
                         @PathVariable String boardId,
                         @Valid BoardDto boardDto,
                         BindingResult bindingResult) throws UnsupportedEncodingException {
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(c -> {
                attributes.addFlashAttribute("message", c.getField() + " : " + c.getDefaultMessage());
            });
        } else if (boardService.update(boardDto, boardId)) {
            attributes.addFlashAttribute("message", "update success");
        } else {
            attributes.addFlashAttribute("message", "update fail");
        }
        return "redirect:/board/detail/" + boardId;

    }


    @GetMapping("/delete/{boardId}")
    public String delete(RedirectAttributes attributes,
                         @PathVariable String boardId) throws UnsupportedEncodingException {
        if (boardService.delete(boardId)) {
            attributes.addFlashAttribute("message", "delete success");
            return "redirect:/board/list";
        } else {
            attributes.addFlashAttribute("message", "delete fail");
            return "redirect:/board/detail/" + boardId;
        }
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String exceptionController() {
        return "redirect:/board/list";
    }
}
