package me.kts.boardexample.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.util.Enumeration;

@Controller
public class IndexController {
    private final String VISIT_TIME = "visitTime";
    private final String ACCOUNT = "account";

    @GetMapping("/")
    public String index(HttpSession session) {
        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            System.out.println(attributeNames.nextElement());
        }
        return "index";
    }
}
