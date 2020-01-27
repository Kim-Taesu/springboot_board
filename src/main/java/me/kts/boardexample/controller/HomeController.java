package me.kts.boardexample.controller;

import me.kts.boardexample.domain.Account;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.validation.Valid;

@Controller
public class HomeController {
    @GetMapping("/")
    public String index(
            Model model,
            @Valid @ModelAttribute Account account,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("message", "로그인을 하세요");
        }
        return "index";
    }
}
