package com.dyds.orders.po;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

import java.io.Serializable;

import java.util.Date;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
* 订单表
* @TableName oms_order
*/
@TableName("oms_order")
@Data
public class OmsOrder implements Serializable {

    /**
    * 订单ID
    */
    @ApiModelProperty("订单ID")
    private Long id;
    /**
    * 用户ID
    */
    @ApiModelProperty("用户ID")
    private Long userId;
    /**
    * 订单号
    */
    @ApiModelProperty("订单号")
    private String orderSn;
     /**
    * 地址ID
    */
    @ApiModelProperty("地址ID")
    private Long addressId;

    /**
    * 创建时间
    */
    @ApiModelProperty("创建时间")
    private Date createTime;
    /**
    * 用户名
    */
    @Size(max= 200,message="编码长度不能超过200")
    @ApiModelProperty("用户名")
    @Length(max= 200,message="编码长度不能超过200")
    private String username;
    /**
    * 订单总金额
    */
    @ApiModelProperty("订单总金额")
    private BigDecimal totalAmount;
    /**
    * 应付总额
    */
    @ApiModelProperty("应付总额")
    private BigDecimal payAmount;
    /**
    * 运费金额
    */
    @ApiModelProperty("运费金额")
    private BigDecimal freightAmount;
    /**
    * 优惠券抵扣金额
    */
    @ApiModelProperty("优惠券抵扣金额")
    private BigDecimal couponAmount;
    /**
    * 支付方式 [1-支付宝，2-微信，3-银联，4-货到付款]
    */
    @ApiModelProperty("支付方式 [1-支付宝，2-微信，3-银联，4-货到付款]")
    private Integer payType;
    /**
    * 订单状态 [0-待付款，1-待发货，2-已发货，3-已完成，4-已关闭，5-无效订单]
    */
    @ApiModelProperty("订单状态 [0-待付款，1-待发货，2-已发货，3-已完成，4-已关闭，5-无效订单]")
    private Integer status;
    /**
    * 更新时间
    */
    @ApiModelProperty("更新时间")
    private Date updatedAt;

}
