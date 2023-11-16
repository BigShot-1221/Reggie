package com.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.dto.SetMealDto;
import com.reggie.entity.SetMeal;

import java.util.List;

public interface SetMealService extends IService<SetMeal> {

    //新增套餐，同时需要保存套餐和菜品的关联关系
    public void setMealWithDIsh(SetMealDto setMealDto);

    //批量删除套餐
    public void removeWithDish(List<Long> ids);
}
