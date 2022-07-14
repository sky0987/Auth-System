package com.itcast.reggie.dto;


import com.itcast.reggie.entity.Setmeal;
import com.itcast.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
