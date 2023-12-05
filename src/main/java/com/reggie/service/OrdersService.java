package com.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.dto.OrdersDto;
import com.reggie.entity.Orders;

public interface OrdersService extends IService<Orders> {

    /**
     * 用户下单
     * @param orders
     */
    void submit(Orders orders);

    Page<OrdersDto> setPage(int page, int pageSize);
}
