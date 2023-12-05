package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.common.BaseContext;
import com.reggie.common.CustomException;
import com.reggie.dto.OrdersDto;
import com.reggie.entity.*;
import com.reggie.mapper.OrdersMapper;
import com.reggie.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 用户下单
     * @param orders
     */
    @Override
    @Transactional
    public void submit(Orders orders) {
        //获得当前用户Id
        Long currentId = BaseContext.getCurrentId();
        //设置一个订单Id
        long orderId = IdWorker.getId();
        //查询当前用户的购物车数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);

        //使用AtomicInteger来计算,是一个原子操作.可以保证在多线程的情况下计算也没有问题.
        AtomicInteger amount = new AtomicInteger(0);

        List<OrderDetail> orderDetails = list.stream().map( (item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setDishId(item.getDishId());
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            //这个方法相当于累加,也就是+=                 multiply相当于乘法,金额乘以份数
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());

        if (list == null || list.size() == 0) {
            throw new CustomException("购物车为空,不能下单");
        }
        //查询用户数据
        User user = userService.getById(currentId);
        //查询地址数据
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        //向订单表插入数据,一条数据
        orders.setUserId(currentId);


        orders.setNumber(String.valueOf(orderId));
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));
        orders.setUserId(currentId);
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName()) +
                (addressBook.getCityName() == null ? "" : addressBook.getCityName()) +
                (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName()) +
                (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        this.save(orders);
        //向订单明细表插入数据,多条数据
        orderDetailService.saveBatch(orderDetails);
        //清空购物车数据
        shoppingCartService.remove(queryWrapper);
    }

    @Override
    public Page<OrdersDto> setPage(int page, int pageSize) {
        Long currentId = BaseContext.getCurrentId();

        Page<Orders> pageInfo = new Page<>(page, pageSize);

        Page<OrdersDto> ordersDtoPage = new Page<>();

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.orderByDesc(Orders::getOrderTime);

        queryWrapper.eq(Orders::getUserId, currentId);

        super.page(pageInfo, queryWrapper);

        BeanUtils.copyProperties(pageInfo, ordersDtoPage, "records");

        List<Orders> list = pageInfo.getRecords();

        LambdaQueryWrapper<OrderDetail> orderDetailLambdaQueryWrapper = new LambdaQueryWrapper<>();

        List<OrdersDto> collect = list.stream().map((item) -> {
            log.info("进入到循环了");
            OrdersDto ordersDto = new OrdersDto();
            //对象拷贝
            BeanUtils.copyProperties(item, ordersDto);

            String number = item.getNumber();

            orderDetailLambdaQueryWrapper.eq(OrderDetail::getOrderId, number);

            int count = orderDetailService.count(orderDetailLambdaQueryWrapper);

            ordersDto.setSumNum(count);

            return ordersDto;
        }).collect(Collectors.toList());

        ordersDtoPage.setRecords(collect);

        return ordersDtoPage;
    }
}
