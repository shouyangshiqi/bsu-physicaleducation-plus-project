package com.dyds.orders.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author khran
 * @version 1.0
 * @description
 */
@Data
public class OrderItem {
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
}
