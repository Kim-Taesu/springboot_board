package me.kts.boardexample.controller;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import me.kts.boardexample.domain.Account;
import me.kts.boardexample.domain.Board;
import me.kts.boardexample.service.BoardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Slf4j
@Controller
@SessionAttributes("account")
public class BoardController {

    private final BoardService service;

    public BoardController(BoardService service) {
        this.service = service;
    }

    @ModelAttribute
    public Account setUpAccount() {
        return new Account();
    }


    @GetMapping("/create")
    public String createPage() {
        return "create";
    }

    @GetMapping("/list")
    public String boardList(Model model,
                            RedirectAttributes attributes,
                            @Valid @ModelAttribute Account account,
                            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            attributes.addFlashAttribute("message", "login을 하세요");
            return "redirect:/account/login";
        } else {
            model.addAttribute("boards", service.viewAll());
            return "list";
        }
    }

    @GetMapping("/detail/{boardId}")
    public String detailPage(Model model,
                             @PathVariable String boardId,
                             RedirectAttributes attributes,
                             @Valid @ModelAttribute Account account,
                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            attributes.addFlashAttribute("message", "login을 하세요");
            return "redirect:/account/login";
        } else {
            Object result = service.detailBoard(boardId);
            if (result.getClass().equals(Board.class)) {
                model.addAttribute("board", result);
                return "detail";
            } else {
                attributes.addFlashAttribute("message", result);
                return "redirect:/list";
            }
        }
    }

    @PostMapping("/create")
    public String create(
            RedirectAttributes attributes,
            @Valid @ModelAttribute Board board,
            @Valid @ModelAttribute Account account,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(c -> {
                attributes.addFlashAttribute("message", c.getField() + " : " + c.getDefaultMessage());
            });
            return "redirect:/create";
        } else {
            attributes.addFlashAttribute("message", service.create(account.getId(), board));
            return "redirect:/list";
        }
    }

    @PostMapping("/update/{boardId}")
    public String update(RedirectAttributes attributes,
                         @Valid @ModelAttribute Account account,
                         @Valid @ModelAttribute Board board,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(c -> {
                attributes.addFlashAttribute("message", c.getField() + " : " + c.getDefaultMessage());
            });
        } else {
            String result = service.update(account.getId(), board);
            attributes.addFlashAttribute("message", result);
        }
        return "redirect:/detail/" + board.getBoardId();
    }

    @GetMapping("/delete/{boardId}")
    public String delete(RedirectAttributes attributes,
                         @PathVariable String boardId,
                         @Valid @ModelAttribute Account account) {
        @NonNull String userId = account.getId();
        String result = service.delete(userId, boardId);
        attributes.addFlashAttribute("message", result);
        if (result.equals("delete success")) {
            return "redirect:/list";
        } else {
            return "redirect:/detail/" + boardId;
        }
    }

    @PostMapping("/comment/{boardId}")
    public String createComment(@PathVariable String boardId,
                                @RequestParam String content,
                                RedirectAttributes attributes,
                                @Valid @ModelAttribute Account account) {
        String userId = account.getId();
        String result = service.createComment(userId, boardId, content);
        attributes.addFlashAttribute("message", result);
        return "redirect:/detail/" + boardId;
    }

    @PostMapping("/update/{boardId}/comment/{commentId}")
    public String updateComment(RedirectAttributes attributes,
                                @PathVariable String boardId,
                                @PathVariable String commentId,
                                @RequestParam String content,
                                @Valid @ModelAttribute Account account) {
        String result = service.updateComment(account.getId(), commentId, boardId, content);
        attributes.addFlashAttribute("message", result);
        return "redirect:/detail/" + boardId;
    }

    @GetMapping("/delete/{boardId}/comment/{commentId}")
    public String deleteComment(RedirectAttributes attributes,
                                @PathVariable String boardId,
                                @PathVariable String commentId,
                                @Valid @ModelAttribute Account account) {
        String result = service.deleteComment(account.getId(), boardId, commentId);
        attributes.addFlashAttribute("message", result);
        return "redirect:/detail/" + boardId;
    }

}
