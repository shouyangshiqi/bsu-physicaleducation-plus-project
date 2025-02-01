package com.dyds.orders.po;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

import java.io.Serializable;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
* 订单商品表
* @TableName oms_order_item
*/

@Data
@TableName("oms_order_item")
public class OmsOrderItem implements Serializable {

    /**
    * 订单项ID
    */
    @ApiModelProperty("订单项ID")
    private Long id;
    /**
    * 订单ID
    */
    @ApiModelProperty("订单ID")
    private Long orderId;
    /**
    * 订单号
    */
    @ApiModelProperty("订单号")
    private String orderSn;
    /**
    * 商品ID
    */
    @ApiModelProperty("商品ID")
    private Long productId;
    /**
    * 商品名称
    */
    @Size(max= 255,message="编码长度不能超过255")
    @ApiModelProperty("商品名称")
    @Length(max= 255,message="编码长度不能超过255")
    private String productName;
    /**
    * 商品图片
    */
    @Size(max= 500,message="编码长度不能超过500")
    @ApiModelProperty("商品图片")
    @Length(max= 500,message="编码长度不能超过500")
    private String productPic;
    /**
    * 商品分类ID
    */
    @ApiModelProperty("商品分类ID")
    private Long categoryId;
    /**
    * 商品购买数量
    */
    @ApiModelProperty("商品购买数量")
    private Integer productQuantity;
    /**
    * 商品销售属性组合（JSON格式）
    */
    @Size(max= 500,message="编码长度不能超过500")
    @ApiModelProperty("商品销售属性组合（JSON格式）")
    @Length(max= 500,message="编码长度不能超过500")
    private String productAttrsVals;
    /**
    * 销售价格
    */
    @ApiModelProperty("销售价格")
    private BigDecimal productPrice;
    /**
    * 优惠券优惠分解金额
    */
    @ApiModelProperty("优惠券优惠分解金额")
    private BigDecimal couponAmount;
    /**
    * 更新时间
    */
    @ApiModelProperty("更新时间")
    private Date updatedAt;

}
