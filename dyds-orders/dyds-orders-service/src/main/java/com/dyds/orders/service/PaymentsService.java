package com.dyds.orders.service;

import com.dyds.orders.dto.CallbackResponse;
import com.dyds.orders.dto.CreatePaymentResponse;
import com.dyds.orders.dto.CreatePaymentRequest;
import com.dyds.orders.dto.PayStatusDto;
import com.dyds.orders.po.OmsPaymentInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author khran
 * @version 1.0
 * @description
 */

public interface PaymentsService {
    /**
     * 新增支付记录并生成支付二维码
     * @param request
     * @return
     */
    public CreatePaymentResponse createPayment(CreatePaymentRequest request);

    /**
     * 根据支付记录id获取支付记录
     * @param paymentId
     * @return
     */
    public OmsPaymentInfo getPayRecordById(String paymentId);

    /**
     * 请求支付宝查询支付结果并更新数据
     * @param paymentId 支付记录id
     * @return 支付记录信息
     */
    public OmsPaymentInfo queryPayResult(String paymentId);

    /**
     * 保存支付宝查询结果到数据库中
     * @param paymentInfo
     * @param payStatusDto
     * @return
     */
    public OmsPaymentInfo saveAliPayStatus(OmsPaymentInfo paymentInfo, PayStatusDto payStatusDto);

    /**
     * 支付宝支付回调
     * @param request
     */
    public CallbackResponse payCallback(HttpServletRequest request) throws Exception;
}
