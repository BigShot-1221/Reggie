package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.BaseContext;
import com.reggie.common.R;
import com.reggie.dto.OrdersDto;
import com.reggie.entity.AddressBook;
import com.reggie.entity.Orders;
import com.reggie.service.AddressBookService;
import com.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private AddressBookService addressBookService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        orders.setOrderTime(LocalDateTime.now());
        ordersService.submit(orders);
        return R.success("下单成功");
    }

    /**
     * 员工页订单列表查看
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/page")
    public R<Page<Orders>> page(int page
            , int pageSize
            , Long number
            , String beginTime
            , String endTime) {
        Page<Orders> ordersPage = new Page<>(page, pageSize);

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(number != null, Orders::getId, number);

        queryWrapper.gt(beginTime != null, Orders::getOrderTime, beginTime);
        queryWrapper.lt(endTime != null, Orders::getOrderTime, endTime);

        ordersService.page(ordersPage, queryWrapper);
        return R.success(ordersPage);
    }

    /**
     * 设置订单状态
     * @param orders
     * @return
     */
    @PutMapping
    public R<String> setStatute(@RequestBody Orders orders) {
        ordersService.updateById(orders);
        return R.success("派送成功");
    }


    @GetMapping("/userPage")
    public R<Page<OrdersDto>> userPage(int page, int pageSize) {

        Page<OrdersDto> ordersDtoPage = ordersService.setPage(page, pageSize);

        return R.success(ordersDtoPage);
    }
}
