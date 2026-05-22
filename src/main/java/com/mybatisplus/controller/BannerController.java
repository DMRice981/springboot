package com.mybatisplus.controller;

import com.mybatisplus.entity.Banner;
import com.mybatisplus.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/banner")
@RequiredArgsConstructor
public class BannerController {
    private final BannerService bannerService;

    @GetMapping("/list")
    public List<Banner> list(){
        return bannerService.list();
    }

    @GetMapping("/get/{id}")
    public Banner get(@PathVariable Integer id){
        return bannerService.getById(id);
    }

    @PostMapping("/add")
    public boolean add(@RequestBody Banner banner){
        return bannerService.save(banner);
    }

    @PutMapping("/update")
    public boolean update(@RequestBody Banner banner){
        return bannerService.updateById(banner);
    }

    @DeleteMapping("/delete/{id}")
    public boolean delete(@PathVariable Integer id){
        return bannerService.removeById(id);
    }
}