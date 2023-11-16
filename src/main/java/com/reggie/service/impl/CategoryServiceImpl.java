package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.common.CustomException;
import com.reggie.entity.Category;
import com.reggie.entity.Dish;
import com.reggie.entity.SetMeal;
import com.reggie.mapper.CategoryMapper;
import com.reggie.service.CategoryService;
import com.reggie.service.DishService;
import com.reggie.service.SetMealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetMealService setMealService;

    /**
     * 根据Id删除分类，删除前进行判断
     *
     * @param id
     */
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件根据分类Id
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        int count = dishService.count(dishLambdaQueryWrapper);
        //查询当前分类是否关联菜品,如果已关联，抛出业务异常
        if (count != 0){
            //已经关联，抛出一个业务异常(定义的全局异常处理器捕获并返回)
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }


        //查询当前分类是否关联套餐,如果已关联，抛出业务异常
        LambdaQueryWrapper<SetMeal> setMealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //根据category进行where查询
        setMealLambdaQueryWrapper.eq(SetMeal::getCategoryId, id);

        int count1 = setMealService.count();

        if (count1 != 0){
            //已经关联了套餐，抛出一个业务异常
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }


        //正常删除
        super.removeById(id);
    }
}
