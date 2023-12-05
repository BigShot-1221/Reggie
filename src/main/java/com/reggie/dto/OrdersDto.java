package com.reggie.dto;

import com.reggie.entity.Orders;
import lombok.Data;

@Data
public class OrdersDto extends Orders {

    private int sumNum;

}
