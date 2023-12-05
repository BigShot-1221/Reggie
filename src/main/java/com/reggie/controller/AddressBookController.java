package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.reggie.common.BaseContext;
import com.reggie.common.R;
import com.reggie.entity.AddressBook;
import com.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 新增收获地址
     * @param addressBook
     * @return
     */
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook){
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }

    /**
     * 展示当前用户收货地址
     * @return
     */
    @GetMapping("/list")
    public R<List<AddressBook>> show() {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        List<AddressBook> list = addressBookService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 设置默认地址
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook) {
        LambdaUpdateWrapper<AddressBook> updateWrapper = new LambdaUpdateWrapper<>();
        //先将该用户关联的地址全部改成0,再将新设置的默认地址改成1
        updateWrapper.set(AddressBook::getIsDefault, 0);
        updateWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        //update address_book set is_default = 0 where user_id = ?
        addressBookService.update(updateWrapper);

        //update address_book set is_default = 1 where id = ?
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);

        return R.success(addressBook);
    }


    /**
     * 根据Id查询单个
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<AddressBook> getById(Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        return R.success(addressBook);
    }

    /**
     * 查询默认地址
     * @return
     */
    @GetMapping("/default")
    public R<AddressBook> getDefault() {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());

        queryWrapper.eq(AddressBook::getIsDefault, 1);

        AddressBook addressBook = addressBookService.getOne(queryWrapper);


        return R.success(addressBook);
    }
}
