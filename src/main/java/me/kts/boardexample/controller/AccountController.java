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


    private final AccountService service;

    public AccountController(AccountService service) {
        this.service = service;
    }

    @GetMapping("/detail")
    public String detail(HttpSession session,
                         Model model) {
        String id = (String) session.getAttribute("id");
        Account account = service.getInfo(id);
        if (account == null) {
            model.addAttribute("message", "fail to get user info");
        } else {
            model.addAttribute("account", account);
        }
        return "account/user-detail";
    }

    @GetMapping("/delete/{accountId}")
    public String delete(HttpSession session,
                         @PathVariable String accountId,
                         Model model) {
        String id = (String) session.getAttribute("id");
        if (id.equals(accountId)) {
            if (service.deleteUser(accountId)) {
                model.addAttribute("message", "delete success");
                return "redirect:/account/logout";
            } else {
                model.addAttribute("message", "delete fail");
            }
        } else {
            model.addAttribute("message", "wrong user");
        }
        return "account/user-detail";
    }

    @PostMapping("/detail")
    public String update(HttpSession session,
                         RedirectAttributes attributes,
                         @Valid Account account) {
        String id = (String) session.getAttribute("id");
        if (account.getId().equals(id)) {
            if (service.updateInfo(account)) {
                attributes.addFlashAttribute("message", "update success");
            } else {
                attributes.addFlashAttribute("message", "update fail");
            }
        } else {
            attributes.addFlashAttribute("message", "wrong user");
        }
        return "redirect:/account/detail";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "account/login";
    }

    @GetMapping("/signUp")
    public String signUpPage() {
        return "account/signUp";
    }

    @PostMapping("/login")
    public String login(@RequestParam String id,
                        @RequestParam String password,
                        RedirectAttributes attributes,
                        HttpSession session) {
        if (service.loginCheck(id, password)) {
            session.setAttribute("id", id);
            return "redirect:/";
        } else {
            attributes.addFlashAttribute("message", "id / pw 오류");
            return "redirect:/account/login";
        }
    }

    @PostMapping("/signUp")
    public String signUp(Model model,
                         @Valid @ModelAttribute Account account,
                         BindingResult bindingResult,
                         RedirectAttributes attributes) {
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(c -> {
                attributes.addFlashAttribute("message", c.getField() + " : " + c.getDefaultMessage());
            });
            return "redirect:/account/signUp";
        }
        if (service.signupcheck(account)) {
            model.addAttribute("message", "signUp success");
            return "account/login";
        } else {
            attributes.addFlashAttribute("message", "signUp fail");
            return "redirect:/account/signUp";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        service.logout(request.getSession());
        return "redirect:/";
    }
}

