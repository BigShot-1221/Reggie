package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.R;
import com.reggie.dto.DishDto;
import com.reggie.dto.SetMealDto;
import com.reggie.entity.Category;
import com.reggie.entity.SetMeal;
import com.reggie.service.CategoryService;
import com.reggie.service.SetMealDishService;
import com.reggie.service.SetMealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */
@Slf4j
@RestController
@RequestMapping("/setMeal")
public class SetMealController {

    @Autowired
    private SetMealService setMealService;
    @Autowired
    private SetMealDishService setMealDishService;
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CacheManager cacheManager;

    /**
     * 新增套餐
     * @param setMealDto
     * @return
     */
    @CacheEvict(value = "setMealCache",  allEntries = true) //将setMealCache下的所有缓存都删除
    @PostMapping
    public R<String> save(@RequestBody SetMealDto setMealDto) {
        log.info("新增套餐");
        setMealService.setMealWithDIsh(setMealDto);

        return R.success("添加成功!");
    }

    /**
     * 套餐分頁查詢
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page<SetMealDto>> page(int page, int pageSize, String name) {

        Page<SetMeal> pageInfo = new Page<>(page, pageSize);

        Page<SetMealDto> setMealDtoPage = new Page<>();

        LambdaQueryWrapper<SetMeal> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.like(name != null, SetMeal::getName, name);

        queryWrapper.orderByDesc(SetMeal::getUpdateTime);

        setMealService.page(pageInfo, queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo, setMealDtoPage, "records");
        List<SetMeal> records = pageInfo.getRecords();

        List<SetMealDto> list = records.stream().map(item -> {
            SetMealDto setMealDto = new SetMealDto();
            //对象拷贝
            BeanUtils.copyProperties(item, setMealDto);
            //分类ID
            Long categoryId = item.getCategoryId();
            //根据分类ID查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                //分类名称
                String categoryName = category.getName();
                setMealDto.setCategoryName(categoryName);
            }
            return setMealDto;
        }).collect(Collectors.toList());

        setMealDtoPage.setRecords(list);

        return R.success(setMealDtoPage);
    }

    /**
     * 删除或批量删除套餐
     * @param ids
     * @return
     */
    @CacheEvict(value = "setMealCache",  allEntries = true) //将setMealCache下的所有缓存都删除
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        log.info("批量删除套餐...");
        setMealService.removeByIds(ids);
        return R.success("删除成功！");
    }

    /**
     * 修改菜品售卖状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> isSell(@PathVariable Integer status,@RequestParam Long ids) {
        LambdaUpdateWrapper<SetMeal> updateWrapper = new LambdaUpdateWrapper<>();

        updateWrapper.eq(SetMeal::getId, ids);
        updateWrapper.set(SetMeal::getStatus, status);

        setMealService.update(updateWrapper);

        return R.success("修改成功");
    }

    /**
     * 根据分类Id查询菜品
     * @param setMeal
     * @return
     */
    @Cacheable(value = "setMealCache", key = "#setMeal.getCategoryId() + '_' + #setMeal.status", unless = "#result == null ")
    @GetMapping("/list")
    public R<List<SetMeal>> list(SetMeal setMeal){
        LambdaQueryWrapper<SetMeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setMeal.getCategoryId() != null, SetMeal::getCategoryId, setMeal.getCategoryId());
        queryWrapper.eq(SetMeal::getStatus, setMeal.getStatus());
        queryWrapper.orderByDesc(SetMeal::getUpdateTime);

        List<SetMeal> list = setMealService.list(queryWrapper);
        return R.success(list);
    }
}

