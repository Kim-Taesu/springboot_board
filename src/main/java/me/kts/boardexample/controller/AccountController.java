package me.kts.boardexample.controller;

import lombok.extern.slf4j.Slf4j;
import me.kts.boardexample.domain.Account;
import me.kts.boardexample.service.AccountService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Enumeration;

@Controller
@RequestMapping(value = "/account")
@Slf4j
public class AccountController {


    private final AccountService service;

    public AccountController(AccountService service) {
        this.service = service;
    }

    @GetMapping("/detail")
    public String detail(Model model) {
        Account account = service.getInfo();
        if (account == null) {
            model.addAttribute("message", "fail to get user info");
            return "index";
        } else {
            model.addAttribute("account", account);
        }
        return "account/user-detail";
    }

    @GetMapping("/delete/{accountId}")
    public String delete(@PathVariable String accountId,
                         RedirectAttributes attributes,
                         HttpSession session,
                         Model model) {
        if (service.deleteUser(accountId)) {
            attributes.addFlashAttribute("message", "delete success");
            return "redirect:/account/loginPage";
        } else {
            model.addAttribute("message", "delete fail");
            return "account/user-detail";
        }
    }

    @PostMapping("/detail")
    public String update(RedirectAttributes attributes,
                         @Valid Account account) {
        if (service.updateInfo(account)) {
            attributes.addFlashAttribute("message", "update success");
        } else {
            attributes.addFlashAttribute("message", "update fail");
        }
        return "redirect:/account/detail";
    }

    @GetMapping("/loginPage")
    public String loginPage(HttpSession session) {
        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            System.out.println(attributeNames.nextElement());
        }
        return "account/login";
    }

    @GetMapping("/signUp")
    public String signUpPage() {
        return "account/signUp";
    }

    @PostMapping("/signUp")
    public String signUp(Model model,
                         @Valid @ModelAttribute Account account,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(c -> {
                model.addAttribute("message", c.getField() + " : " + c.getDefaultMessage());
            });
            return "/account/signUp";
        }
        if (service.signUpCheck(account)) {
            if (service.signUp(account)) {
                model.addAttribute("message", "signUp success");
                return "account/login";
            }
        }
        model.addAttribute("message", "signUp fail");
        return "/account/signUp";
    }
}

