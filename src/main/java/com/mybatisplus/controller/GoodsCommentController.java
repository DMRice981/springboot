package com.mybatisplus.controller;

import com.mybatisplus.common.Result;
import com.mybatisplus.entity.GoodsComment;
import com.mybatisplus.service.GoodsCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class GoodsCommentController {

    private final GoodsCommentService goodsCommentService;

    @GetMapping("/list")
    public Result<List<GoodsComment>> list(@RequestParam(required = false) Integer goodsId) {
        if (goodsId != null) {
            return Result.success(goodsCommentService.lambdaQuery()
                    .eq(GoodsComment::getGoodsId, goodsId)
                    .orderByDesc(GoodsComment::getCreateTime)
                    .list());
        }
        return Result.success(goodsCommentService.lambdaQuery()
                .orderByDesc(GoodsComment::getCreateTime)
                .list());
    }

    @GetMapping("/get/{id}")
    public Result<GoodsComment> get(@PathVariable Integer id) {
        GoodsComment comment = goodsCommentService.getById(id);
        if (comment == null) {
            return Result.error("评论不存在");
        }
        return Result.success(comment);
    }

    @PostMapping("/add")
    public Result<GoodsComment> add(@RequestBody GoodsComment goodsComment) {
        goodsComment.setCreateTime(LocalDateTime.now());
        goodsCommentService.save(goodsComment);
        return Result.success("评论成功", goodsComment);
    }

    @PutMapping("/update")
    public Result<GoodsComment> update(@RequestBody GoodsComment goodsComment) {
        goodsCommentService.updateById(goodsComment);
        return Result.success("更新成功", goodsComment);
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        goodsCommentService.removeById(id);
        return Result.success();
    }
}
