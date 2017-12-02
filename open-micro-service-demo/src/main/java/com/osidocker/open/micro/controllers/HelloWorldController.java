package com.osidocker.open.micro.controllers;

import com.osidocker.open.micro.service.IUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/hello")
public class HelloWorldController {

    @Resource
    private IUserService userService;

    @GetMapping("/world")
    public String hello(){
        return "hello wrold";
    }

    @GetMapping("/user/{name}")
    public Map<String, Object> getUserInfo(@PathVariable("name") String name){
        return userService.findUserByName(name);
    }

    @GetMapping("/get/{name}")
    public Map<String, Object> haveUserInfo(@PathVariable("name") String name){
        return userService.getUserByName(name);
    }
}
