package com.mybatisplus.controller;

import com.mybatisplus.common.Result;
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
    public Result<List<Banner>> list() {
        return Result.success(bannerService.lambdaQuery()
                .eq(Banner::getStatus, 1)
                .orderByAsc(Banner::getSort)
                .list());
    }

    @GetMapping("/get/{id}")
    public Result<Banner> get(@PathVariable Integer id) {
        Banner banner = bannerService.getById(id);
        if (banner == null) {
            return Result.error("轮播图不存在");
        }
        return Result.success(banner);
    }

    @GetMapping("/listAll")
    public Result<List<Banner>> listAll() {
        return Result.success(bannerService.list());
    }

    @PostMapping("/add")
    public Result<Banner> add(@RequestBody Banner banner) {
        if (banner.getLinkUrl() == null) {
            banner.setLinkUrl("");
        }
        if (banner.getStatus() == null) {
            banner.setStatus(1);
        }
        if (banner.getSort() == null) {
            banner.setSort(0);
        }
        bannerService.save(banner);
        return Result.success("添加成功", banner);
    }

    @PutMapping("/update")
    public Result<Banner> update(@RequestBody Banner banner) {
        if (banner.getLinkUrl() == null) {
            banner.setLinkUrl("");
        }
        if (banner.getStatus() == null) {
            banner.setStatus(1);
        }
        bannerService.updateById(banner);
        return Result.success("更新成功", banner);
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        bannerService.removeById(id);
        return Result.success();
    }
}
