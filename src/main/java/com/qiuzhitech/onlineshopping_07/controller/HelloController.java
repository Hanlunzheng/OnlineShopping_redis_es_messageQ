package com.qiuzhitech.onlineshopping_07.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/hello")
    public String HelloWorld() {
        return "Hello world";
    }

    @GetMapping("/echo/{path}")
    public String echo(@PathVariable("path") String path) {
        return "Your Path from url should end with:" + path;
    }


}
