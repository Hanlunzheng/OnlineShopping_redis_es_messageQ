package com.qiuzhitech.onlineshopping_07.controller;


import com.qiuzhitech.onlineshopping_07.model.UserDemo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

//@RestController
@Controller
@ResponseBody
public class UserController {
    Map<String, UserDemo> users = new HashMap<>();
    @PostMapping("/users")
    public String createUser(@RequestParam("name") String name,@RequestParam("email") String email){
        UserDemo user = UserDemo.builder().name(name).email(email).build();
        users.put(email, user);
        return user.toString();

    }
    @GetMapping("/users/{name}")
    public String getUser(@PathVariable("name") String name, Map<String, Object> showData){
        UserDemo user = users.get(name);
        showData.put("user", user);
        return "user_detail";
    }


}
