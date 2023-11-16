package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.R;
import com.reggie.entity.Category;
import com.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类管理
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("category:{}", category);
        boolean save = categoryService.save(category);
        if (save){
            return  R.success("添加成功！");
        }
        return R.error("添加失败！");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize){
        //分页构造器
        Page<Category> pageInfo = new Page(page, pageSize);

        //条件构造器
        LambdaQueryWrapper<Category> queryWapper = new LambdaQueryWrapper<>();

        //添加排序条件(根据sort升序排序)
        queryWapper.orderByAsc(Category::getSort);

        //进行分页查询
        categoryService.page(pageInfo, queryWapper);

        return R.success(pageInfo);
    }

    /**
     * 根据Id删除日志
     * @param id
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long id) {
        log.info("删除Id为：{}", id);

        categoryService.remove(id);

        return R.success("分类信息删除成功！");
    }

    /**
     * 修改信息
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){

        log.info("修改分类信息：{}", category);
        categoryService.updateById(category);

        return R.success("修改分类信息成功！");
    }

    /**
     * 根据条件查询分类数据
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category) {
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.eq(category.getType() != null, Category::getType, category.getType() );
        //添加排序条件(优先使用sort排序，sort相同的情况下使用修改时间来排序)
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(queryWrapper);

        return R.success(list);
    }
}
