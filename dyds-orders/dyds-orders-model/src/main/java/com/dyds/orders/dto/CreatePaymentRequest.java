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
public class CreatePaymentRequest {

    private Long orderId;
    private BigDecimal totalAmount;
    private Integer payType;

}
