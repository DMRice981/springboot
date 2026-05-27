package com.mybatisplus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mybatisplus.common.Constants;
import com.mybatisplus.common.Result;
import com.mybatisplus.entity.User;
import com.mybatisplus.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public Result<User> login(@RequestBody User loginUser, HttpSession session) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, loginUser.getUsername())
               .eq(User::getPassword, loginUser.getPassword());
        User user = userService.getOne(wrapper);
        if (user == null) {
            return Result.error("用户名或密码错误");
        }
        if (user.getStatus().equals(Constants.Status.DISABLED)) {
            return Result.error("账户已被禁用");
        }
        // 存储用户信息到session
        session.setAttribute("user", user);
        return Result.success("登录成功", user);
    }

    @PostMapping("/register")
    public Result<User> register(@RequestBody User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return Result.error("密码不能为空");
        }
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, user.getUsername());
        User existUser = userService.getOne(wrapper);
        if (existUser != null) {
            return Result.error("用户名已存在");
        }
        user.setCreateTime(LocalDateTime.now());
        user.setStatus(Constants.Status.ENABLED);
        user.setIsDelete(Constants.Status.NOT_DELETED);
        userService.save(user);
        return Result.success("注册成功", user);
    }

    @GetMapping("/list")
    public Result<List<User>> list() {
        List<User> list = userService.lambdaQuery()
                .eq(User::getIsDelete, Constants.Status.NOT_DELETED)
                .orderByDesc(User::getCreateTime)
                .list();
        return Result.success(list);
    }

    @GetMapping("/get/{id}")
    public Result<User> get(@PathVariable Integer id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return Result.success(user);
    }

    @PutMapping("/update")
    public Result<User> update(@RequestBody User user) {
        if (user.getId() == null) {
            return Result.error("用户ID不能为空");
        }
        User existUser = userService.getById(user.getId());
        if (existUser == null) {
            return Result.error("用户不存在");
        }
        user.setIsDelete(null);
        user.setUpdateTime(LocalDateTime.now());
        userService.updateById(user);
        return Result.success("更新成功", userService.getById(user.getId()));
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        userService.lambdaUpdate()
                .eq(User::getId, id)
                .set(User::getIsDelete, Constants.Status.DELETED)
                .set(User::getUpdateTime, LocalDateTime.now())
                .update();
        return Result.success();
    }

    @PostMapping("/status")
    public Result<Void> updateStatus(@RequestParam Integer id, @RequestParam Integer status) {
        if (id == null) {
            return Result.error("用户ID不能为空");
        }
        if (status == null || (status != Constants.Status.ENABLED && status != Constants.Status.DISABLED)) {
            return Result.error("状态值无效");
        }
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        userService.lambdaUpdate()
                .eq(User::getId, id)
                .set(User::getStatus, status)
                .set(User::getUpdateTime, LocalDateTime.now())
                .update();
        return Result.success();
    }
}
