package com.mybatisplus.controller;

import com.mybatisplus.entity.User;
import com.mybatisplus.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/list")
    public List<User> list(){
        return userService.list();
    }

    @GetMapping("/get/{id}")
    public User get(@PathVariable Integer id){
        return userService.getById(id);
    }

    @PostMapping("/add")
    public boolean add(@RequestBody User user){
        return userService.save(user);
    }

    @PutMapping("/update")
    public boolean update(@RequestBody User user){
        return userService.updateById(user);
    }

    @DeleteMapping("/delete/{id}")
    public boolean delete(@PathVariable Integer id){
        return userService.removeById(id);
    }
}