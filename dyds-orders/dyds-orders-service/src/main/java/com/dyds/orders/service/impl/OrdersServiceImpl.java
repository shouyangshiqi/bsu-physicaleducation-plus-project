package com.dyds.orders.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dyds.orders.dto.*;
import com.dyds.orders.mapper.OmsOrderItemMapper;
import com.dyds.orders.mapper.OmsOrderMapper;
import com.dyds.orders.po.OmsOrder;
import com.dyds.orders.po.OmsOrderItem;
import com.dyds.orders.service.OrdersService;
import com.dyds.orders.utils.OrderSnGenerator;
import com.dyds.orders.utils.execption.DydsException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author khran
 * @version 1.0
 * @description
 */

@Service

public class OrdersServiceImpl implements OrdersService {

    @Autowired
    private  OmsOrderMapper omsOrderMapper;

    @Autowired
    private OmsOrderItemMapper omsOrderItemMapper;

    @Override
    @Transactional
    public CreateOrderResponse createOrdersBase(CreateOrderRequest createOrderRequest) {
        // 创建订单对象
        OmsOrder omsOrder = new OmsOrder();
        omsOrder.setUserId(createOrderRequest.getUserId());
        omsOrder.setAddressId(createOrderRequest.getAddressId());
        // 这儿金额有点乱（payAmount，couponAmount，freightAmount，totalAmount）
        BigDecimal totalAmount = getTotalAmount(createOrderRequest.getItems());
        omsOrder.setPayAmount(totalAmount);
        BigDecimal couponAmount = getCouponAmount(createOrderRequest.getCouponId());
        omsOrder.setCouponAmount(couponAmount);
        BigDecimal freightAmount = new BigDecimal(10);
        omsOrder.setFreightAmount(freightAmount);
        omsOrder.setTotalAmount(totalAmount.subtract(couponAmount).add(freightAmount));
        omsOrder.setPayType(createOrderRequest.getPayType());
        // 补全用户名
        String userName = getUserName(createOrderRequest.getUserId());
        omsOrder.setUsername(userName);
        // 补全订单号
        String orderSn = OrderSnGenerator.generateOrderSn();
        omsOrder.setOrderSn(orderSn);
        // 补全创建时间
        omsOrder.setCreateTime(new Date());
        // 补全订单状态(未支付）
        omsOrder.setStatus(0);

        // 插入订单到数据库
        int insert = omsOrderMapper.insert(omsOrder);
        if(insert <= 0){
            DydsException.cast("插入订单失败");
        }

        // 将订单商品信息插入订单商品数据库
        insertOrderItem(createOrderRequest.getItems(), omsOrder);


        // 返回响应
        CreateOrderResponse response = new CreateOrderResponse();
        response.setCode(200);
        response.setData(new CreateOrderResponse.Data(omsOrder.getId()));
        response.setMessage("Success");
        return response;
    }

    @Override
    public Order getOrderById(Long orderId) {
        OmsOrder omsOrder = omsOrderMapper.selectById(orderId);
        Order order = new Order();
        BeanUtils.copyProperties(omsOrder, order);
        order.setOrderId(orderId);
        List<OmsOrderItem> omsOrderItems = omsOrderItemMapper.selectList(new LambdaQueryWrapper<OmsOrderItem>().eq(OmsOrderItem::getOrderId, orderId));
        ArrayList<OrderItem> orderItems = new ArrayList<>();
        for (OmsOrderItem omsOrderItem : omsOrderItems) {
            OrderItem orderItem = new OrderItem();
            BeanUtils.copyProperties(omsOrderItem, orderItem);
            orderItem.setQuantity(omsOrderItem.getProductQuantity());
            orderItem.setPrice(omsOrderItem.getProductPrice());
            orderItems.add(orderItem);
        }
        order.setItems(orderItems);

        // TODO 根据地址id查询地址信息
        OrderAddress orderAddress = new OrderAddress();
        orderAddress.setName("张三");
        orderAddress.setPhone("12345678901");
        orderAddress.setDetail("北京市朝阳区");
        order.setAddress(orderAddress);
        return order;
    }

    /**
     * TODO 根据商品查询订单总金额
     * @param items
     * @return
     */
    private BigDecimal getTotalAmount(List<CreateOrderRequest.Item> items) {
        BigDecimal totalAmount = new BigDecimal(0);
        for (CreateOrderRequest.Item item : items) {
            //TODO 查询商品价格
            BigDecimal price = new BigDecimal(100);
            // 计算总价
            totalAmount = totalAmount.add(price.multiply(new BigDecimal(item.getQuantity())));
        }
        return totalAmount;
    }

    /**
     * TODO 根据优惠券id查询优惠券金额
     * @param couponId
     * @return
     */
    private BigDecimal getCouponAmount(Long couponId) {
        //TODO 查询优惠券金额
        return new BigDecimal(20);
    }

    /**
     * TODO 根据用户id查询用户名
     * @param userId
     * @return
     */
    private String getUserName(Long userId) {
        return "zijiechonglangying";
    }

    /**
     * @param items
     */
    @Transactional
    public void insertOrderItem(List<CreateOrderRequest.Item> items, OmsOrder omsOrder) {
        for (CreateOrderRequest.Item item : items) {
            // TODO 根据商品id查询商品信息

            //TODO 将商品信息保存到商品订单
            OmsOrderItem omsOrderItem = new OmsOrderItem();

            omsOrderItem.setProductQuantity(item.getQuantity());
            omsOrderItem.setOrderId(omsOrder.getId());
            omsOrderItem.setOrderSn(omsOrder.getOrderSn());

            int insert = omsOrderItemMapper.insert(omsOrderItem);
            if(insert <= 0){
                throw new RuntimeException("插入订单商品失败");
            }
        }

    }

}
