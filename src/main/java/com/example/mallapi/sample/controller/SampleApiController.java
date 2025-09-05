package com.example.mallapi.sample.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sample")
public class SampleApiController {

    @GetMapping("/hello")
    public String hello() {
        return   "hello";
    }

    @GetMapping("/hello2")
    public String[] hello2(){
        return new String[]{"Hello", "World"};
    }
}
