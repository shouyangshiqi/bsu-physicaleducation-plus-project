package com.dyds.orders.service;

import com.dyds.orders.dto.CreateOrderResponse;
import com.dyds.orders.dto.CreateOrderRequest;
import com.dyds.orders.dto.Order;

/**
 * @author khran
 * @version 1.0
 * @description
 */

public interface OrdersService {

    /**
     * 创建订单服务
     * @param createOrderRequest 前端传入相关参数
     * @return
     */
    CreateOrderResponse createOrdersBase(CreateOrderRequest createOrderRequest);

    /**
     * 根据订单id查询订单信息
     * @param orderId
     * @return
     */
    Order getOrderById(Long orderId);
}
