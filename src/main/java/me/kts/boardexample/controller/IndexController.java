package me.kts.boardexample.controller;

import me.kts.boardexample.common.CurrentUser;
import me.kts.boardexample.domain.Account;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {
    @GetMapping("/")
    public String index(@CurrentUser Account account) {
        return "index";
    }
}
