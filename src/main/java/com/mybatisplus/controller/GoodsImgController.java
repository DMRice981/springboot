package com.mybatisplus.controller;

import com.mybatisplus.common.Result;
import com.mybatisplus.entity.GoodsImg;
import com.mybatisplus.service.GoodsImgService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/goodsImg")
@RequiredArgsConstructor
public class GoodsImgController {

    private final GoodsImgService goodsImgService;

    @GetMapping("/list")
    public Result<List<GoodsImg>> list(@RequestParam(required = false) Integer goodsId) {
        if (goodsId != null) {
            return Result.success(goodsImgService.lambdaQuery()
                    .eq(GoodsImg::getGoodsId, goodsId)
                    .orderByAsc(GoodsImg::getSort)
                    .list());
        }
        return Result.success(goodsImgService.list());
    }

    @GetMapping("/get/{id}")
    public Result<GoodsImg> get(@PathVariable Integer id) {
        GoodsImg img = goodsImgService.getById(id);
        if (img == null) {
            return Result.error("图片不存在");
        }
        return Result.success(img);
    }

    @PostMapping("/add")
    public Result<GoodsImg> add(@RequestBody GoodsImg goodsImg) {
        goodsImgService.save(goodsImg);
        return Result.success("添加成功", goodsImg);
    }

    @PutMapping("/update")
    public Result<GoodsImg> update(@RequestBody GoodsImg goodsImg) {
        goodsImgService.updateById(goodsImg);
        return Result.success("更新成功", goodsImg);
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        goodsImgService.removeById(id);
        return Result.success();
    }
}
