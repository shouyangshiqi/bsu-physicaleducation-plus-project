package com.dyds.orders.api;

import com.dyds.orders.dto.CreateOrderRequest;
import com.dyds.orders.dto.CreateOrderResponse;
import com.dyds.orders.dto.Order;
import com.dyds.orders.service.impl.OrdersServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author khran
 * @version 1.0
 * @description
 */

@RestController
@RequestMapping("/orders")
@Api(value = "订单信息编辑接口",tags = "订单信息编辑接口")
public class OrdersController {

    @Autowired
    private OrdersServiceImpl ordersService;
    @ApiOperation("新增订单基础信息")
    @PostMapping("/create")
    public CreateOrderResponse createOrdersBase(@RequestBody CreateOrderRequest createOrderRequest){
        CreateOrderResponse createOrderResponse = ordersService.createOrdersBase(createOrderRequest);
        return createOrderResponse;
    }

    @ApiOperation("订单id查询接口")
    @GetMapping("/{orderId}")
    public Order getOrderById(@PathVariable Long orderId){

        return ordersService.getOrderById(orderId);
    }
}
