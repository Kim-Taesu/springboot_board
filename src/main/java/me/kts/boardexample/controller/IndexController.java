package me.kts.boardexample.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {
    private final String VISIT_TIME = "visitTime";
    private final String ACCOUNT = "account";

    @GetMapping("/")
    public String index() {
        return "index";
    }
}
