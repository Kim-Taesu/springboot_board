package me.kts.boardexample.controller;

import me.kts.boardexample.domain.Account;
import me.kts.boardexample.domain.Board;
import me.kts.boardexample.service.BoardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.jws.WebParam;
import javax.servlet.http.HttpSession;

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

    @GetMapping("/detail")
    public String detailPage(
            Model model,
            @RequestParam String id,
            HttpSession httpSession,
            RedirectAttributes attributes) {
        Object account = httpSession.getAttribute("account");
        if (account == null) {
            attributes.addFlashAttribute("message", "login을 하세요");
            return "redirect:/account/login";
        } else {
            Object result = service.detailBoard(id);
            if (result.getClass().equals(Board.class)) {
                model.addAttribute("board", (Board) result);
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

    @PostMapping("/update")
    public String update(
            HttpSession httpSession,
            RedirectAttributes attributes,
            @RequestParam String id,
            @RequestParam String title,
            @RequestParam String content) {
        Account account = (Account) httpSession.getAttribute("account");
        attributes.addFlashAttribute("message", service.update(account.getId(), id, title, content));
        return "redirect:/list";
    }

    @DeleteMapping("/delete")
    public String delete(
            HttpSession httpSession,
            Model model,
            RedirectAttributes attributes,
            @PathVariable String id) {
        Account account = (Account) httpSession.getAttribute("account");

        service.delete(id);
        return "redirect:/list";
    }


}
