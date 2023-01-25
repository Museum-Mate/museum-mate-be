package com.dev.museummate.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/example")
public class ExampleController {

    // 시큐리티 테스트를 위한 임의의 endpoint
    @GetMapping("/security")
    public String securityTest(Authentication authentication){
        return "security test success";
    }

    @GetMapping("/security/admin")
    public String securityTestAdmin(Authentication authentication){
        return "security test success";
    }
}
