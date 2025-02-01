package com.dyds.orders.po;

import javax.validation.constraints.Size;

import java.io.Serializable;

import java.math.BigDecimal;
import java.util.Date;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
* 支付信息表
* @TableName oms_payment_info
*/
@Data
public class OmsPaymentInfo implements Serializable {

    /**
    * 支付记录ID
    */
    @ApiModelProperty("支付记录ID")
    private Long id;
    /**
    * 订单号 (对外业务号)
    */
    @ApiModelProperty("订单号 (对外业务号)")
    private String orderSn;
    /**
    * 订单ID
    */
    @ApiModelProperty("订单ID")
    private Long orderId;
    /**
    * 支付宝交易流水号
    */
    @Size(max= 50,message="编码长度不能超过50")
    @ApiModelProperty("支付宝交易流水号")
    @Length(max= 50,message="编码长度不能超过50")
    private String alipayTradeNo;
    /**
    * 支付总金额
    */
    @ApiModelProperty("支付总金额")
    private BigDecimal totalAmount;
    /**
    * 交易内容
    */
    @Size(max= 200,message="编码长度不能超过200")
    @ApiModelProperty("交易内容")
    @Length(max= 200,message="编码长度不能超过200")
    private String subject;
    /**
    * 支付状态
    */
    @Size(max= 20,message="编码长度不能超过20")
    @ApiModelProperty("支付状态")
    @Length(max= 20,message="编码长度不能超过20")
    private String paymentStatus;
    /**
    * 创建时间
    */
    @ApiModelProperty("创建时间")
    private Date createTime;
    /**
    * 确认时间
    */
    @ApiModelProperty("确认时间")
    private Date confirmTime;
    /**
    * 回调内容
    */
    @Size(max= 4000,message="编码长度不能超过4000")
    @ApiModelProperty("回调内容")
    private String callbackContent;
    /**
    * 回调时间
    */
    @ApiModelProperty("回调时间")
    private Date callbackTime;
    /**
    * 更新时间
    */
    @ApiModelProperty("更新时间")
    private Date updatedAt;
}
