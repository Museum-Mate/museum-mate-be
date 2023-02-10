package com.dev.museummate.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestView {

    @GetMapping("/")
    public String index() {
        return "index";
    }


    @GetMapping("/login")
    public String login() {
        return "login";
    }


}
