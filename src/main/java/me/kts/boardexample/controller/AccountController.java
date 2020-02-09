package me.kts.boardexample.controller;

import lombok.extern.slf4j.Slf4j;
import me.kts.boardexample.domain.Account;
import me.kts.boardexample.service.AccountService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@RequestMapping(value = "/account")
@Slf4j
public class AccountController {

    private final String ACCOUNT = "account";

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
    public String login(@RequestParam String id,
                        @RequestParam String password,
                        RedirectAttributes attributes,
                        HttpSession session) {
        Object result = service.login(id, password);
        if (result.getClass().equals(Account.class)) {
            session.setAttribute(ACCOUNT, result);
            return "redirect:/";
        } else {
            attributes.addFlashAttribute("message", result);
            return "redirect:/account/login";
        }
    }

    @PostMapping("/signUp")
    public String signUp(Model model,
                         @Valid @ModelAttribute Account account,
                         BindingResult bindingResult,
                         RedirectAttributes attributes,
                         HttpSession session) {
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(c -> {
                attributes.addFlashAttribute("message", c.getField() + " : " + c.getDefaultMessage());
            });
            return "redirect:/account/signUp";
        }
        String result = service.signUp(account);
        if (result.equals("success")) {
            log.info("signUp success");
            model.addAttribute("message", result);
            return "login";
        } else {
            log.info("signUp fail");
            attributes.addFlashAttribute("message", result);
            return "redirect:/account/signUp";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().removeAttribute(ACCOUNT);
        return "redirect:/";
    }
}

