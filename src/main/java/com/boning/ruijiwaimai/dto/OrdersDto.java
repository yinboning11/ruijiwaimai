package com.boning.ruijiwaimai.dto;

import com.boning.ruijiwaimai.entity.OrderDetailEntity;
import com.boning.ruijiwaimai.entity.OrdersEntity;
import lombok.Data;

import java.util.List;

@Data
public class OrdersDto extends OrdersEntity {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetailEntity> orderDetails;

}
