package me.kts.boardexample.controller;

import lombok.extern.slf4j.Slf4j;
import me.kts.boardexample.domain.Board;
import me.kts.boardexample.domain.BoardDto;
import me.kts.boardexample.service.BoardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Slf4j
@Controller
public class BoardController {

    private final String ACCOUNT = "id";
    private final BoardService service;

    public BoardController(BoardService service) {
        this.service = service;
    }

    @GetMapping("/create")
    public String createPage() {
        return "create";
    }

    @GetMapping("/list")
    public String boardList(Model model) {
        model.addAttribute("boards", service.viewAll());
        return "list";
    }

    @GetMapping("/detail/{boardId}")
    public String detailPage(Model model,
                             @PathVariable String boardId,
                             RedirectAttributes attributes) {
        Board board = service.detailBoard(boardId);
        if (board == null) {
            attributes.addFlashAttribute("message", "detail board error");
            return "redirect:/list";
        } else {
            model.addAttribute("board", board);
            return "detail";
        }
    }

    @PostMapping("/create")
    public String create(
            RedirectAttributes attributes,
            @Valid BoardDto boardDto,
            HttpSession session,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(c -> {
                attributes.addFlashAttribute("message", c.getField() + " : " + c.getDefaultMessage());
            });
            return "redirect:/create";
        }

        if (service.create(session.getAttribute("id").toString(), boardDto)) {
            attributes.addFlashAttribute("message", "게시글 생성 성공");
            return "redirect:/list";
        } else {
            return "redirect:/create";
        }
    }

    @PostMapping("/update/{boardId}")
    public String update(RedirectAttributes attributes,
                         HttpSession session,
                         @Valid @ModelAttribute Board board,
                         BindingResult bindingResult) {
        String id = (String) session.getAttribute("id");
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(c -> {
                attributes.addFlashAttribute("message", c.getField() + " : " + c.getDefaultMessage());
            });
        } else if (service.update(id, board)) {
            attributes.addFlashAttribute("message", "update success");
        } else {
            attributes.addFlashAttribute("message", "update fail");
        }
        return "redirect:/detail/" + board.getBoardId();

    }

    @GetMapping("/delete/{boardId}")
    public String delete(RedirectAttributes attributes,
                         @PathVariable String boardId,
                         HttpSession session) {
        String userId = (String) session.getAttribute("id");
        if (service.delete(userId, boardId)) {
            attributes.addFlashAttribute("message", "delete success");
            return "redirect:/list";
        } else {
            attributes.addFlashAttribute("message", "delete fail");
            return "redirect:/detail/" + boardId;
        }
    }

    @PostMapping("/comment/{boardId}")
    public String createComment(@PathVariable String boardId,
                                @RequestParam String content,
                                RedirectAttributes attributes,
                                HttpSession session) {
        String userId = (String) session.getAttribute("id");
        if (service.createComment(userId, boardId, content)) {
            attributes.addFlashAttribute("message", "create comment success");
        } else {
            attributes.addFlashAttribute("message", "create comment fail");
        }
        return "redirect:/detail/" + boardId;
    }

    @PostMapping("/update/{boardId}/comment/{commentId}")
    public String updateComment(RedirectAttributes attributes,
                                @PathVariable String boardId,
                                @PathVariable String commentId,
                                @RequestParam String content,
                                HttpSession session) {
        String id = (String) session.getAttribute("id");
        String message;
        if (service.updateComment(id, commentId, boardId, content)) {
            message = "update comment success";
        } else {
            message = "update comment fail";
        }
        attributes.addFlashAttribute("message", message);
        return "redirect:/detail/" + boardId;
    }

    @GetMapping("/delete/{boardId}/comment/{commentId}")
    public String deleteComment(RedirectAttributes attributes,
                                @PathVariable String boardId,
                                @PathVariable String commentId,
                                HttpSession session) {
        String id = (String) session.getAttribute("id");
        String message;
        if (service.deleteComment(id, boardId, commentId)) {
            message = "delete comment success";
        } else {
            message = "delete comment fail";
        }
        attributes.addFlashAttribute("message", message);
        return "redirect:/detail/" + boardId;
    }

}
