package com.reggie.dto;

import com.reggie.entity.SetMeal;
import com.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetMealDto extends SetMeal {

    private List<SetmealDish> setMealDishes;

    private String categoryName;
}
