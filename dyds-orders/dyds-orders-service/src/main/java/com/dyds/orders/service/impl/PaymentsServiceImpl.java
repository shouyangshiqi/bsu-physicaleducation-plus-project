package com.dyds.orders.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dyds.orders.config.AlipayConfig;
import com.dyds.orders.dto.*;
import com.dyds.orders.mapper.OmsOrderMapper;
import com.dyds.orders.mapper.OmsPaymentInfoMapper;
import com.dyds.orders.po.OmsOrder;
import com.dyds.orders.po.OmsPaymentInfo;
import com.dyds.orders.service.PaymentsService;
import com.dyds.orders.utils.QRCodeUtil;
import com.dyds.orders.utils.execption.DydsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author khran
 * @version 1.0
 * @description
 */
@Service
@Slf4j
public class PaymentsServiceImpl implements PaymentsService {
    @Autowired
    private OmsOrderMapper omsOrderMapper;

    @Autowired
    private PaymentsService paymentsService;


    @Autowired
    private OmsPaymentInfoMapper omsPaymentInfoMapper;

    @Value("${pay.qrcodeurl}")
    String qrcodeurl;

    @Value("${pay.alipay.APP_ID}")
    String APP_ID;
    @Value("${pay.alipay.APP_PRIVATE_KEY}")
    String APP_PRIVATE_KEY;

    @Value("${pay.alipay.ALIPAY_PUBLIC_KEY}")
    String ALIPAY_PUBLIC_KEY;

    @Override
    @Transactional
    public CreatePaymentResponse createPayment(CreatePaymentRequest request) {
        Long orderId = request.getOrderId();

        OmsOrder omsOrder = omsOrderMapper.selectById(orderId);
        if(omsOrder == null){
            DydsException.cast("订单不存在");
        }

        OmsPaymentInfo omsPaymentInfo = omsPaymentInfoMapper.selectOne(new LambdaQueryWrapper<OmsPaymentInfo>().eq(OmsPaymentInfo::getOrderId, orderId));

        if(omsPaymentInfo != null){
            //支付状态
            String status = omsPaymentInfo.getPaymentStatus();
            if(!"WAIT_BUYER_PAY".equals(status)){
                DydsException.cast("订单已支付或订单已经关闭");
            }
        }else {
            // 创建支付记录
            omsPaymentInfo = new OmsPaymentInfo();
            omsPaymentInfo.setOrderId(omsOrder.getId());
            omsPaymentInfo.setOrderSn(omsOrder.getOrderSn());
            omsPaymentInfo.setTotalAmount(request.getTotalAmount());
            omsPaymentInfo.setPaymentStatus("WAIT_BUYER_PAY");
            // TODO 交易内容
            omsPaymentInfo.setSubject("交易内容");
            int insert = omsPaymentInfoMapper.insert(omsPaymentInfo);
            if(insert <= 0){
                DydsException.cast("创建支付记录失败");
            }
        }

        // 生成二维码
        String qrCode = null;
        try {
            //url要可以被模拟器访问到，url为下单接口
            String url = String.format(qrcodeurl, omsPaymentInfo.getId());
            qrCode = new QRCodeUtil().createQRCode(url, 200, 200);
        } catch (IOException e) {
            DydsException.cast("生成二维码出错");
        }

        CreatePaymentResponse response = new CreatePaymentResponse();
        response.setCode(200);
        response.setQrcode(qrCode);
        response.setData(new CreatePaymentResponse.Data(omsPaymentInfo.getId()));
        response.setMessage("Success");
        return response;
    }

    @Override
    public OmsPaymentInfo getPayRecordById(String paymentId) {
        OmsPaymentInfo omsPaymentInfo = omsPaymentInfoMapper.selectById(Long.parseLong(paymentId));
        if(omsPaymentInfo == null){
            DydsException.cast("支付记录不存在");
        }
        return omsPaymentInfo;
    }

    @Override
    @Transactional
    public OmsPaymentInfo queryPayResult(String paymentId) {
        OmsPaymentInfo paymentInfo = getPayRecordById(paymentId);
        if (paymentInfo == null) {
            DydsException.cast("支付记录不存在");
        }
        //支付状态
        String status = paymentInfo.getPaymentStatus();
        //如果支付成功直接返回
        if (!"WAIT_BUYER_PAY".equals(status)) {
            return paymentInfo;
        }
        //从支付宝查询支付结果
        PayStatusDto payStatusDto = queryPayResultFromAlipay(paymentId);
        //保存支付结果
        return paymentsService.saveAliPayStatus(paymentInfo, payStatusDto);
    }
    /**
     * 请求支付宝查询支付结果
     * @param paymentId 支付交易号
     * @return 支付结果
     */
    public PayStatusDto queryPayResultFromAlipay(String paymentId) {

        //========请求支付宝查询支付结果=============
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.URL, APP_ID, APP_PRIVATE_KEY, "json", AlipayConfig.CHARSET, ALIPAY_PUBLIC_KEY, AlipayConfig.SIGNTYPE); //获得初始化的AlipayClient
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", paymentId);
        request.setBizContent(bizContent.toString());
        AlipayTradeQueryResponse response = null;
        try {
            response = alipayClient.execute(request);
            if (!response.isSuccess()) {
                DydsException.cast("请求支付查询查询失败");
            }
        } catch (AlipayApiException e) {
            log.error("请求支付宝查询支付结果异常:{}", e.toString(), e);
            DydsException.cast("请求支付查询查询失败");
        }

        //获取支付结果
        String resultJson = response.getBody();
        //转map
        Map resultMap = JSON.parseObject(resultJson, Map.class);
        Map alipay_trade_query_response = (Map) resultMap.get("alipay_trade_query_response");
        try {
            //支付结果
            String trade_status = (String) alipay_trade_query_response.get("trade_status");
            String total_amount = (String) alipay_trade_query_response.get("total_amount");
            String trade_no = (String) alipay_trade_query_response.get("trade_no");
            String send_pay_date_String = (String) alipay_trade_query_response.get("send_pay_date");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdf.parse(send_pay_date_String);
            //保存支付结果
            PayStatusDto payStatusDto = new PayStatusDto();
            payStatusDto.setOut_trade_no(paymentId);
            payStatusDto.setTrade_status(trade_status);
            payStatusDto.setApp_id(APP_ID);
            payStatusDto.setTrade_no(trade_no);
            payStatusDto.setTotal_amount(total_amount);
            payStatusDto.setGmt_payment(date);
            return payStatusDto;
        } catch (ParseException e) {
            DydsException.cast("解析支付结果异常");
        }
        return null;
    }

    @Transactional
    public OmsPaymentInfo saveAliPayStatus(OmsPaymentInfo paymentInfo, PayStatusDto payStatusDto) {
        paymentInfo.setAlipayTradeNo(payStatusDto.getTrade_no());
        paymentInfo.setConfirmTime(payStatusDto.getGmt_payment());
        paymentInfo.setPaymentStatus(payStatusDto.getTrade_status());
        int update = omsPaymentInfoMapper.updateById(paymentInfo);
        if(update <= 0){
            DydsException.cast("查询并更新支付记录失败");
        }
        Long orderId = paymentInfo.getOrderId();
        OmsOrder omsOrder = omsOrderMapper.selectById(orderId);
        omsOrder.setStatus(1);
        update = omsOrderMapper.updateById(omsOrder);
        if(update <= 0){
            DydsException.cast("更新订单状态失败");
        }
        return paymentInfo;
    }

    @Override
    public CallbackResponse payCallback(HttpServletRequest request) throws Exception {
        Map<String,String> params = new HashMap<String,String>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }

        //验签
        boolean verify_result = AlipaySignature.rsaCheckV1(params, ALIPAY_PUBLIC_KEY, AlipayConfig.CHARSET, "RSA2");

        if(verify_result) {//验证成功

            //商户订单号
            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
            //支付宝交易号
            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");
            //交易状态
            String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");
            //appid
            String app_id = new String(request.getParameter("app_id").getBytes("ISO-8859-1"),"UTF-8");
            //total_amount
            String total_amount = new String(request.getParameter("total_amount").getBytes("ISO-8859-1"),"UTF-8");
            //gmt_payment
            String gmt_payment = new String(request.getParameter("gmt_payment").getBytes("ISO-8859-1"),"UTF-8");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdf.parse(gmt_payment);

            PayStatusDto payStatusDto = new PayStatusDto();
            payStatusDto.setOut_trade_no(out_trade_no);
            payStatusDto.setTrade_status(trade_status);
            payStatusDto.setApp_id(APP_ID);
            payStatusDto.setTrade_no(trade_no);
            payStatusDto.setTotal_amount(total_amount);
            payStatusDto.setGmt_payment(date);

            //交易成功处理
            if (trade_status.equals("TRADE_SUCCESS")) {
                OmsPaymentInfo omsPaymentInfo = omsPaymentInfoMapper.selectById(out_trade_no);
                if(omsPaymentInfo == null){
                    DydsException.cast("支付记录不存在");
                }else if(!omsPaymentInfo.getPaymentStatus().equals("WAIT_BUYER_PAY")){
                    DydsException.cast("支付记录已处理");
                }
                paymentsService.saveAliPayStatus(omsPaymentInfo, payStatusDto);
            }
            return new CallbackResponse(200,"success", null);
        }else {
            DydsException.cast("回调验签失败");
            return null;
        }
    }
}
