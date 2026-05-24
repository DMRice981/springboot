package com.mybatisplus.controller;

import com.mybatisplus.common.Result;
import com.mybatisplus.entity.UserAddress;
import com.mybatisplus.service.UserAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
public class UserAddressController {

    private final UserAddressService userAddressService;

    @GetMapping("/list")
    public Result<List<UserAddress>> list(@RequestParam(required = false) Integer userId) {
        if (userId != null) {
            return Result.success(userAddressService.lambdaQuery()
                    .eq(UserAddress::getUserId, userId)
                    .orderByDesc(UserAddress::getIsDefault)
                    .orderByDesc(UserAddress::getCreateTime)
                    .list());
        }
        return Result.success(userAddressService.list());
    }

    @GetMapping("/get/{id}")
    public Result<UserAddress> get(@PathVariable Integer id) {
        UserAddress address = userAddressService.getById(id);
        if (address == null) {
            return Result.error("地址不存在");
        }
        return Result.success(address);
    }

    @PostMapping("/add")
    public Result<UserAddress> add(@RequestBody UserAddress userAddress) {
        userAddressService.save(userAddress);
        return Result.success("添加成功", userAddress);
    }

    @PutMapping("/update")
    public Result<UserAddress> update(@RequestBody UserAddress userAddress) {
        userAddressService.updateById(userAddress);
        return Result.success("更新成功", userAddress);
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        userAddressService.removeById(id);
        return Result.success();
    }
}
