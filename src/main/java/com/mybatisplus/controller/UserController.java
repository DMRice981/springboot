package com.mybatisplus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mybatisplus.common.Result;
import com.mybatisplus.entity.User;
import com.mybatisplus.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<User> login(@RequestBody User loginUser) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, loginUser.getUsername())
               .eq(User::getPassword, loginUser.getPassword());
        User user = userService.getOne(wrapper);
        if (user == null) {
            return Result.error("用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            return Result.error("账户已被禁用");
        }
        return Result.success("登录成功", user);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<User> register(@RequestBody User user) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, user.getUsername());
        User existUser = userService.getOne(wrapper);
        if (existUser != null) {
            return Result.error("用户名已存在");
        }
        user.setCreateTime(LocalDateTime.now());
        user.setStatus(1);
        user.setIsDelete(0);
        userService.save(user);
        return Result.success("注册成功", user);
    }

    /**
     * 获取所有用户列表
     */
    @GetMapping("/list")
    public Result<List<User>> list() {
        List<User> list = userService.lambdaQuery()
                .eq(User::getIsDelete, 0)
                .orderByDesc(User::getCreateTime)
                .list();
        return Result.success(list);
    }

    /**
     * 获取单个用户信息
     */
    @GetMapping("/get/{id}")
    public Result<User> get(@PathVariable Integer id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return Result.success(user);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    public Result<User> update(@RequestBody User user) {
        user.setUpdateTime(LocalDateTime.now());
        userService.updateById(user);
        return Result.success("更新成功", user);
    }

    /**
     * 删除用户（逻辑删除）
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        userService.lambdaUpdate()
                .eq(User::getId, id)
                .set(User::getIsDelete, 1)
                .set(User::getUpdateTime, LocalDateTime.now())
                .update();
        return Result.success("删除成功");
    }

    /**
     * 更新用户状态
     */
    @PostMapping("/status")
    public Result<Void> updateStatus(@RequestParam Integer id, @RequestParam Integer status) {
        userService.lambdaUpdate()
                .eq(User::getId, id)
                .set(User::getStatus, status)
                .set(User::getUpdateTime, LocalDateTime.now())
                .update();
        return Result.success();
    }
}
