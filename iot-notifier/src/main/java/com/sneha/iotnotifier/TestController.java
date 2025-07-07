package com.sneha.iotnotifier;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/ping")
    public String ping() {
        return " Spring Boot is up and serving requests!";
    }
}
