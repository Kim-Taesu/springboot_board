package me.kts.boardexample.controller;

import lombok.extern.slf4j.Slf4j;
import me.kts.boardexample.domain.Account;
import me.kts.boardexample.service.AccountService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping(value = "/account")
@SessionAttributes("account")
@Slf4j
public class AccountController {

    private final AccountService service;

    public AccountController(AccountService service) {
        this.service = service;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/signUp")
    public String signUpPage() {
        return "signUp";
    }

    @PostMapping("/login")
    public String login(Model model,
                        HttpSession httpSession,
                        @RequestParam String id,
                        @RequestParam String password,
                        RedirectAttributes attributes) {
        Object account = httpSession.getAttribute("account");
        Object result = service.login(id, password);
        if (result.getClass().equals(Account.class)) {
            model.addAttribute("account", result);
            return "redirect:/list";
        } else {
            attributes.addFlashAttribute("message", result);
            return "redirect:/account/login";
        }
    }

    @PostMapping("/signUp")
    public String signUp(Model model,
                         @RequestParam String id,
                         @RequestParam String password,
                         @RequestParam String name,
                         RedirectAttributes attributes) {
        String result = service.signUp(id, password, name);
        if (result.equals("success")) {
            model.addAttribute("message", result);
            return "login";
        } else {
            attributes.addFlashAttribute("message", result);
            return "redirect:/account/signUp";
        }
    }

    @GetMapping("/logout")
    public String logout(SessionStatus status) {
        status.setComplete();
        return "index";
    }


}

