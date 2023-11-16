package com.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.entity.DishFlavor;
import com.reggie.mapper.DIshFlavorMapper;
import com.reggie.service.DIshFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DIshFlavorMapper, DishFlavor> implements DIshFlavorService {
}
