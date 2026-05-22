package com.mybatisplus.controller;

import com.mybatisplus.entity.UserAddress;
import com.mybatisplus.service.UserAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserAddressController {

    private final UserAddressService userAddressService;

    @GetMapping("/userAddress/list")
    public List<UserAddress> list(){
        return userAddressService.list();
    }

    @GetMapping("/userAddress/get/{id}")
    public UserAddress get(@PathVariable Integer id){
        return userAddressService.getById(id);
    }

    @PostMapping("/userAddress/add")
    public boolean add(@RequestBody UserAddress userAddress){
        return userAddressService.save(userAddress);
    }

    @PutMapping("/userAddress/update")
    public boolean update(@RequestBody UserAddress userAddress){
        return userAddressService.updateById(userAddress);
    }

    @DeleteMapping("/userAddress/delete/{id}")
    public boolean delete(@PathVariable Integer id){
        return userAddressService.removeById(id);
    }
}