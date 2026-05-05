package com.mybatisplus.controller;

import com.mybatisplus.entity.Admin;
import com.mybatisplus.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/list")
    public List<Admin> list(){
        return adminService.list();
    }

    @GetMapping("/get/{id}")
    public Admin get(@PathVariable Integer id){
        return adminService.getById(id);
    }

    @PostMapping("/add")
    public boolean add(@RequestBody Admin admin){
        return adminService.save(admin);
    }

    @PutMapping("/update")
    public boolean update(@RequestBody Admin admin){
        return adminService.updateById(admin);
    }

    @DeleteMapping("/delete/{id}")
    public boolean delete(@PathVariable Integer id){
        return adminService.removeById(id);
    }
}