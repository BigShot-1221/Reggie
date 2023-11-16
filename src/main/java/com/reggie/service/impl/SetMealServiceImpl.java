package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.common.CustomException;
import com.reggie.common.R;
import com.reggie.dto.SetMealDto;
import com.reggie.entity.SetMeal;
import com.reggie.entity.SetmealDish;
import com.reggie.mapper.SetMealMapper;
import com.reggie.service.SetMealDishService;
import com.reggie.service.SetMealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetMealServiceImpl extends ServiceImpl<SetMealMapper, SetMeal> implements SetMealService {

    @Autowired
    private SetMealDishService setMealDishService;

    /**
     * 新增套餐信息
     * @param setMealDto
     */
    @Override
    @Transactional
    public void setMealWithDIsh(SetMealDto setMealDto) {
        //保存套餐的基本信息，操作setMeal，执行insert操作
        this.save(setMealDto);

        //保存套餐和菜品的关联信息，操作setMeal_dish，执行insert操作
        List<SetmealDish> list = setMealDto.getSetMealDishes();

        list = list.stream().map(item -> {
            item.setSetmealId(setMealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setMealDishService.saveBatch(list);
    }

    /**
     * 批量删除套餐以及套餐关联数据
     * @param ids
     */
    @Override
    public void removeWithDish(List<Long> ids) {
        //查询套餐状态，确定是否可以删除     select count(*) from setMeal where id in ? and status = 1
        LambdaQueryWrapper<SetMeal> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.in(SetMeal::getId, ids);

        queryWrapper.eq(SetMeal::getStatus, 1);

        int count = this.count(queryWrapper);

        //如果不能删除，抛出一个业务异常
        if (count > 0){
            throw new CustomException("套餐正在售卖中，不能删除");
        }
        //如果可以删除，先删除套餐表中的数据----setMeal
        this.removeByIds(ids);
        //删除关系表中的数据----setMeal-dish;   delete from setMeal_dish where setMeal_id in ()
        LambdaQueryWrapper<SetmealDish> query = new LambdaQueryWrapper<>();
        query.in(SetmealDish::getSetmealId, ids);

        setMealDishService.remove(query);
    }
}
