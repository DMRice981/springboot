package com.mybatisplus.controller;

import com.mybatisplus.entity.GoodsComment;
import com.mybatisplus.service.GoodsCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/goodsComment")
public class GoodsCommentController {

    @Autowired
    private GoodsCommentService goodsCommentService;

    @GetMapping("/list")
    public List<GoodsComment> list(){
        return goodsCommentService.list();
    }

    @GetMapping("/get/{id}")
    public GoodsComment get(@PathVariable Integer id){
        return goodsCommentService.getById(id);
    }

    @PostMapping("/add")
    public boolean add(@RequestBody GoodsComment goodsComment){
        return goodsCommentService.save(goodsComment);
    }

    @PutMapping("/update")
    public boolean update(@RequestBody GoodsComment goodsComment){
        return goodsCommentService.updateById(goodsComment);
    }

    @DeleteMapping("/delete/{id}")
    public boolean delete(@PathVariable Integer id){
        return goodsCommentService.removeById(id);
    }
}