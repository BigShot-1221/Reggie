package com.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.entity.SetmealDish;
import com.reggie.mapper.SetMealDishMapper;
import org.springframework.stereotype.Service;
import com.reggie.service.SetMealDishService;

@Service
public class SetMealDishServiceImpl extends ServiceImpl<SetMealDishMapper, SetmealDish> implements SetMealDishService {
}
