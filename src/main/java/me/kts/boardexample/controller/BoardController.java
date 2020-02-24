package me.kts.boardexample.controller;

import lombok.extern.slf4j.Slf4j;
import me.kts.boardexample.domain.Board;
import me.kts.boardexample.domain.BoardDto;
import me.kts.boardexample.domain.CommentDto;
import me.kts.boardexample.service.BoardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;

@Slf4j
@Controller
@RequestMapping(value = "/board")
public class BoardController {

    private final BoardService service;

    public BoardController(BoardService service) {
        this.service = service;
    }

    @GetMapping("/create")
    public String createPage() {
        return "board/create";
    }

    @GetMapping("/list")
    public String boardList(Model model) {
        model.addAttribute("boards", service.viewAll());
        return "board/list";
    }

    @GetMapping("/detail/{boardId}")
    public String detailPage(Model model,
                             @PathVariable String boardId,
                             RedirectAttributes attributes) {
        Board board = service.detailBoard(boardId);
        if (board == null) {
            attributes.addFlashAttribute("message", "detail board error");
            return "redirect:/board/list";
        } else {
            model.addAttribute("board", board);
            return "board/detail";
        }
    }

    @PostMapping(value = "/create")
    public String create(
            RedirectAttributes attributes,
            @Valid BoardDto boardDto,
            HttpSession session,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(c -> {
                attributes.addFlashAttribute("message", c.getField() + " : " + c.getDefaultMessage());
            });
            return "redirect:/board/create";
        }

        if (service.create(session.getAttribute("id").toString(), boardDto)) {
            attributes.addFlashAttribute("message", "게시글 생성 성공");
            return "redirect:/board/list";
        } else {
            return "redirect:/board/create";
        }
    }

    @PostMapping("/update/{boardId}")
    public String update(RedirectAttributes attributes,
                         @PathVariable String boardId,
                         @Valid BoardDto boardDto,
                         BindingResult bindingResult,
                         Principal principal) {
        String id = principal.getName();
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(c -> {
                attributes.addFlashAttribute("message", c.getField() + " : " + c.getDefaultMessage());
            });
        } else if (service.update(id, boardDto, boardId)) {
            attributes.addFlashAttribute("message", "update success");
        } else {
            attributes.addFlashAttribute("message", "update fail");
        }
        return "redirect:/board/detail/" + boardId;

    }

    @GetMapping("/delete/{boardId}")
    public String delete(RedirectAttributes attributes,
                         @PathVariable String boardId,
                         Principal principal) {
        String userId = principal.getName();
        if (service.delete(userId, boardId)) {
            attributes.addFlashAttribute("message", "delete success");
            return "redirect:/board/list";
        } else {
            attributes.addFlashAttribute("message", "delete fail");
            return "redirect:/board/detail/" + boardId;
        }
    }

    @PostMapping("/comment/{boardId}")
    public String createComment(@PathVariable String boardId,
                                @Valid CommentDto commentDto,
                                RedirectAttributes attributes,
                                Principal principal) {
        String userId = principal.getName();
        if (service.createComment(userId, boardId, commentDto.getComment())) {
            attributes.addFlashAttribute("message", "create comment success");
        } else {
            attributes.addFlashAttribute("message", "create comment fail");
        }
        return "redirect:/board/detail/" + boardId;
    }

    @PostMapping("/update/{boardId}/comment/{commentId}")
    public String updateComment(RedirectAttributes attributes,
                                @PathVariable String boardId,
                                @PathVariable String commentId,
                                @Valid CommentDto commentDto,
                                Principal principal) {
        String id = principal.getName();
        String message;
        if (service.updateComment(id, commentId, boardId, commentDto.getComment())) {
            message = "update comment success";
        } else {
            message = "update comment fail";
        }
        attributes.addFlashAttribute("message", message);
        return "redirect:/board/detail/" + boardId;
    }

    @GetMapping("/delete/{boardId}/comment/{commentId}")
    public String deleteComment(RedirectAttributes attributes,
                                @PathVariable String boardId,
                                @PathVariable String commentId,
                                Principal principal) {
        String id = principal.getName();
        String message;
        if (service.deleteComment(id, boardId, commentId)) {
            message = "delete comment success";
        } else {
            message = "delete comment fail";
        }
        attributes.addFlashAttribute("message", message);
        return "redirect:/board/detail/" + boardId;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String exceptionController() {
        return "redirect:/board/list";
    }

}
