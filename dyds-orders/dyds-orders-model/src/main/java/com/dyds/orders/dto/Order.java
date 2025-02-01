package com.dyds.orders.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author khran
 * @version 1.0
 * @description
 */

@Data
public class Order {
    private Long orderId;
    private String orderSn;
    private Integer status;
    private BigDecimal totalAmount;
    private List<OrderItem> items;
    private OrderAddress address;
}
