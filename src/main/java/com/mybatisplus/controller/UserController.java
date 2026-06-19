package com.mybatisplus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mybatisplus.common.Constants;
import com.mybatisplus.common.Result;
import com.mybatisplus.dto.PageResult;
import com.mybatisplus.entity.User;
import com.mybatisplus.service.UserService;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "用户模块", description = "用户登录、注册、个人信息管理")
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    @Tag(name = "用户登录", description = "用户登录接口")
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

    /**
     * 分页获取用户列表
     *
     * @param pageNum 页码（默认1）
     * @param pageSize 每页条数（默认10）
     * @param keyword 关键词搜索（可选，匹配用户名或手机号）
     * @param status 用户状态筛选（可选，1正常 0禁用）
     * @return 分页后的用户列表
     */
    @GetMapping("/list/paged")
    public Result<PageResult<User>> listPaged(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getIsDelete, Constants.Status.NOT_DELETED);
        
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(User::getUsername, keyword)
                              .or()
                              .like(User::getPhone, keyword));
        }
        
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        
        wrapper.orderByDesc(User::getCreateTime);
        
        Page<User> page = new Page<>(pageNum, pageSize);
        IPage<User> pageResult = userService.page(page, wrapper);
        
        PageResult<User> result = new PageResult<>(
                pageResult.getTotal(),
                pageNum,
                pageSize,
                pageResult.getRecords()
        );
        
        return Result.success(result);
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
