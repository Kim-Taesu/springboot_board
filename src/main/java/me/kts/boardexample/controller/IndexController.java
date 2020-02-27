package me.kts.boardexample.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Controller
@Slf4j
public class IndexController {
    @GetMapping("/")
    public String index(HttpSession session) {
        return "index";
    }
}
