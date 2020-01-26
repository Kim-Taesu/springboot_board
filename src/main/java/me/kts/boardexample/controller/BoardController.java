package me.kts.boardexample.controller;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import me.kts.boardexample.domain.Account;
import me.kts.boardexample.domain.Board;
import me.kts.boardexample.service.BoardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Slf4j
@Controller
public class BoardController {

    private final BoardService service;

    public BoardController(BoardService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    @GetMapping("/create")
    public String createPage() {
        return "create";
    }

    @GetMapping("/list")
    public String boardList(Model model, HttpSession httpSession) {
        Object account = httpSession.getAttribute("account");
        if (account == null) {
            return "login";
        } else {
            model.addAttribute("boards", service.viewAll());
            return "list";
        }
    }

    @GetMapping("/detail/{id}")
    public String detailPage(
            Model model,
            @PathVariable String id,
            HttpSession httpSession,
            RedirectAttributes attributes) {
        Object account = httpSession.getAttribute("account");
        if (account == null) {
            attributes.addFlashAttribute("message", "login을 하세요");
            return "redirect:/account/login";
        } else {
            Object result = service.detailBoard(id);
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
            @RequestParam String title,
            @RequestParam String content,
            HttpSession httpSession) {
        Account account = (Account) httpSession.getAttribute("account");
        attributes.addFlashAttribute("message", service.create(account.getId(), title, content));
        return "redirect:/list";
    }

    @PostMapping("/update/{id}")
    public String update(
            HttpSession httpSession,
            RedirectAttributes attributes,
            @PathVariable String id,
            @RequestParam String title,
            @RequestParam String content) {
        Account account = (Account) httpSession.getAttribute("account");
        String result = service.update(account.getId(), id, title, content);
        attributes.addFlashAttribute("message", result);
//        return "redirect:/list";
        return "redirect:/detail/" + id;
    }

    @GetMapping("/delete/{id}")
    public String delete(
            HttpSession httpSession,
            RedirectAttributes attributes,
            @PathVariable String id) {
        Account account = (Account) httpSession.getAttribute("account");
        @NonNull String userId = account.getId();
        String result = service.delete(userId, id);
        attributes.addFlashAttribute("message", result);
        if (result.equals("delete success")) {
            return "redirect:/list";
        } else {
            return "redirect:/detail/" + id;
        }
    }

    @PostMapping("/comment/{id}")
    public String createComment(
            HttpSession httpSession,
            @PathVariable String id,
            @RequestParam String content,
            RedirectAttributes attributes) {
        Account account = (Account) httpSession.getAttribute("account");
        String userId = account.getId();

        String result = service.createComment(userId, id, content);
        attributes.addFlashAttribute("message", result);
        return "redirect:/detail/" + id;
    }

    @PostMapping("/update/{boardId}/comment/{id}")
    public String updateComment(
            HttpSession httpSession,
            RedirectAttributes attributes,
            @PathVariable String boardId,
            @PathVariable String id,
            @RequestParam String content) {
        Account account = (Account) httpSession.getAttribute("account");
        String result = service.updateComment(account.getId(), id, boardId, content);
        attributes.addFlashAttribute("message", result);
        return "redirect:/detail/" + boardId;
    }

    @GetMapping("/delete/{boardId}/comment/{id}")
    public String deleteComment(
            HttpSession httpSession,
            RedirectAttributes attributes,
            @PathVariable String boardId,
            @PathVariable String id) {
        Account account = (Account) httpSession.getAttribute("account");
        String result = service.deleteComment(account.getId(), boardId, id);
        attributes.addFlashAttribute("message", result);
        return "redirect:/detail/" + boardId;
    }

}
