package me.kts.boardexample.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class IndexController {

    @GetMapping("/")
    public String index() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        return "index";
    }
}
