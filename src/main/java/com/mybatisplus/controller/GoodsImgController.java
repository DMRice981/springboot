package com.mybatisplus.controller;

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
    public List<GoodsImg> list(){
        return goodsImgService.list();
    }

    @GetMapping("/get/{id}")
    public GoodsImg get(@PathVariable Integer id){
        return goodsImgService.getById(id);
    }

    @PostMapping("/add")
    public boolean add(@RequestBody GoodsImg goodsImg){
        return goodsImgService.save(goodsImg);
    }

    @PutMapping("/update")
    public boolean update(@RequestBody GoodsImg goodsImg){
        return goodsImgService.updateById(goodsImg);
    }

    @DeleteMapping("/delete/{id}")
    public boolean delete(@PathVariable Integer id){
        return goodsImgService.removeById(id);
    }
}