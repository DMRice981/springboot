package com.mybatisplus.controller;

import com.mybatisplus.common.Result;
import com.mybatisplus.entity.GoodsComment;
import com.mybatisplus.entity.User;
import com.mybatisplus.service.GoodsCommentService;
import com.mybatisplus.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class GoodsCommentController {

    private final GoodsCommentService goodsCommentService;
    private final UserService userService;

    @GetMapping("/list")
    public Result<List<GoodsComment>> list(@RequestParam(required = false) Integer goodsId) {
        List<GoodsComment> comments;
        if (goodsId != null) {
            comments = goodsCommentService.lambdaQuery()
                    .eq(GoodsComment::getGoodsId, goodsId)
                    .orderByDesc(GoodsComment::getCreateTime)
                    .list();
        } else {
            comments = goodsCommentService.lambdaQuery()
                    .orderByDesc(GoodsComment::getCreateTime)
                    .list();
        }

        for (GoodsComment comment : comments) {
            if (comment.getUserId() != null) {
                User user = userService.getById(comment.getUserId());
                if (user != null) {
                    comment.setUserName(user.getUsername());
                }
            }
        }

        return Result.success(comments);
    }

    @GetMapping("/get/{id}")
    public Result<GoodsComment> get(@PathVariable Integer id) {
        GoodsComment comment = goodsCommentService.getById(id);
        if (comment == null) {
            return Result.error("评论不存在");
        }
        if (comment.getUserId() != null) {
            User user = userService.getById(comment.getUserId());
            if (user != null) {
                comment.setUserName(user.getUsername());
            }
        }
        return Result.success(comment);
    }

    @PostMapping("/add")
    public Result<GoodsComment> add(@RequestBody java.util.Map<String, Object> payload) {
        try {
            Integer goodsId = payload.get("goodsId") != null ? 
                Integer.valueOf(payload.get("goodsId").toString()) : null;
            Integer userId = payload.get("userId") != null ? 
                Integer.valueOf(payload.get("userId").toString()) : null;
            String content = payload.get("content") != null ? 
                payload.get("content").toString() : null;
            Integer score = payload.get("score") != null ? 
                Integer.valueOf(payload.get("score").toString()) : 5;

            if (goodsId == null) {
                return Result.error(400, "商品ID不能为空");
            }
            if (userId == null) {
                return Result.error(400, "用户ID不能为空");
            }
            if (content == null || content.trim().isEmpty()) {
                return Result.error(400, "评论内容不能为空");
            }

            GoodsComment comment = new GoodsComment();
            comment.setGoodsId(goodsId);
            comment.setUserId(userId);
            comment.setScore(score);
            comment.setContent(content.trim());
            comment.setOrderNo("");
            comment.setCommentImg("");
            goodsCommentService.save(comment);

            return Result.success("评论成功", comment);
        } catch (Exception e) {
            System.err.println("评论发布异常: " + e.getMessage());
            e.printStackTrace();
            return Result.error(500, "评论失败: " + e.getMessage());
        }
    }

    @PutMapping("/update")
    public Result<GoodsComment> update(@RequestBody GoodsComment goodsComment) {
        if (goodsComment.getId() == null) {
            return Result.error(400, "评论ID不能为空");
        }
        goodsCommentService.updateById(goodsComment);
        return Result.success("更新成功", goodsComment);
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        goodsCommentService.removeById(id);
        return Result.success();
    }
}
