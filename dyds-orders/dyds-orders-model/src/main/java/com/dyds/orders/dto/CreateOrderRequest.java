package com.dyds.orders.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author khran
 * @version 1.0
 * @description
 */
@Data
public class CreateOrderRequest  {
    private Long userId;
    private List<Item> items;
    private Long addressId;
    private Long couponId;
    private Integer payType;

    @Data
    public static class Item {
        private Long productId;
        private Integer quantity;
    }
}
