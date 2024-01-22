package com.example.basicSecurity.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/demo")
public class ControllerDemo {

    @GetMapping
    public ResponseEntity<String> sayHello(){
        return ResponseEntity.ok("Hello From Our API");
    }

    @GetMapping("say-good-bye")
    public ResponseEntity<String> sayGoodBye(){
        return ResponseEntity.ok("Good Bye and See You Later");
    }
}
