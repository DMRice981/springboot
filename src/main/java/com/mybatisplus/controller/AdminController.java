package com.mybatisplus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mybatisplus.common.Result;
import com.mybatisplus.entity.Admin;
import com.mybatisplus.service.AdminService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "管理员模块", description = "管理员登录、账号管理")
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/login")
    @Tag(name = "管理员登录", description = "管理员登录接口")
    public Result<Admin> login(@RequestBody Admin loginAdmin, HttpSession session) {
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Admin::getAdminName, loginAdmin.getAdminName())
               .eq(Admin::getPassword, loginAdmin.getPassword());
        Admin admin = adminService.getOne(wrapper);
        if (admin == null) {
            return Result.error("账号或密码错误");
        }
        if (admin.getStatus() != null && admin.getStatus() == 0) {
            return Result.error("账户已被禁用");
        }
        // 存储管理员信息到session
        session.setAttribute("admin", admin);
        return Result.success("登录成功", admin);
    }

    @GetMapping("/list")
    public Result<List<Admin>> list() {
        return Result.success(adminService.list());
    }

    @GetMapping("/get/{id}")
    public Result<Admin> get(@PathVariable Integer id) {
        Admin admin = adminService.getById(id);
        if (admin == null) {
            return Result.error("管理员不存在");
        }
        return Result.success(admin);
    }

    @PostMapping("/add")
    public Result<Admin> add(@RequestBody Admin admin) {
        adminService.save(admin);
        return Result.success("添加成功", admin);
    }

    @PutMapping("/update")
    public Result<Admin> update(@RequestBody Admin admin) {
        adminService.updateById(admin);
        return Result.success("更新成功", admin);
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        adminService.removeById(id);
        return Result.success();
    }
}
