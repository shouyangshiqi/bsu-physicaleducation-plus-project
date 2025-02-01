package com.dyds.orders.api;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.dyds.orders.config.AlipayConfig;
import com.dyds.orders.dto.CallbackResponse;
import com.dyds.orders.dto.CreateOrderResponse;
import com.dyds.orders.dto.CreatePaymentRequest;
import com.dyds.orders.dto.CreatePaymentResponse;
import com.dyds.orders.po.OmsPaymentInfo;
import com.dyds.orders.service.PaymentsService;
import com.dyds.orders.service.impl.PaymentsServiceImpl;
import com.dyds.orders.utils.execption.DydsException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author khran
 * @version 1.0
 * @description
 */

@RestController
@RequestMapping("/payments")
@Api(value = "支付信息编辑接口",tags = "支付信息编辑接口")
public class PaymentsController {

    @Autowired
    private PaymentsService paymentsService;

    @Value("${pay.alipay.APP_ID}")
    String APP_ID;
    @Value("${pay.alipay.APP_PRIVATE_KEY}")
    String APP_PRIVATE_KEY;

    @Value("${pay.alipay.ALIPAY_PUBLIC_KEY}")
    String ALIPAY_PUBLIC_KEY;

    @PostMapping("/create")
    @ApiOperation("新增支付记录")
    public CreatePaymentResponse creatPaymentBase(@RequestBody CreatePaymentRequest createPaymentRequest){
        return paymentsService.createPayment(createPaymentRequest);
    }

    @ApiOperation("扫码下单接口")
    @GetMapping("/requestpay")
    public void requestpay(String paymentId, HttpServletResponse httpResponse) throws IOException {
        //如果payNo不存在则提示重新发起支付
        OmsPaymentInfo paymentInfo = paymentsService.getPayRecordById(paymentId);
        if(paymentInfo == null){
            DydsException.cast("请重新点击生成支付信息");
        }
        //支付状态
        String status = paymentInfo.getPaymentStatus();
        if(!"WAIT_BUYER_PAY".equals(status)){
            DydsException.cast("订单已支付或订单已经关闭");
        }
        //构造sdk的客户端对象
        AlipayClient client = new DefaultAlipayClient(AlipayConfig.URL, APP_ID, APP_PRIVATE_KEY, AlipayConfig.FORMAT, AlipayConfig.CHARSET, ALIPAY_PUBLIC_KEY, AlipayConfig.SIGNTYPE);//获得初始化的AlipayClient
        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();//创建API对应的request
//        alipayRequest.setNotifyUrl("http://166.66.66.66:63040/api/payments/callback");//在公共参数中设置回跳和通知地址
        alipayRequest.setBizContent("{" +
                " \"out_trade_no\":\""+paymentInfo.getId()+"\"," +
                " \"total_amount\":\""+paymentInfo.getTotalAmount().floatValue() +"\"," +
                " \"subject\":\""+paymentInfo.getSubject()+"\"," +
                " \"product_code\":\"QUICK_WAP_PAY\"" +
                " }");//填充业务参数
        String form = "";
        try {
            //请求支付宝下单接口,发起http请求
            form = client.pageExecute(alipayRequest).getBody(); //调用SDK生成表单
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        httpResponse.setContentType("text/html;charset=" + AlipayConfig.CHARSET);
        httpResponse.getWriter().write(form);//直接将完整的表单html输出到页面
        httpResponse.getWriter().flush();
        httpResponse.getWriter().close();
    }

    @ApiOperation("查询支付记录")
    @GetMapping("/paymentInfo/{paymentId}")
    @ResponseBody
    public OmsPaymentInfo getPayRecordById(@PathVariable String paymentId){
        return paymentsService.getPayRecordById(paymentId);
    }
    @ApiOperation("查询支付结果并更新支付记录")
    @GetMapping("/payresult/{paymentId}")
    @ResponseBody
    public OmsPaymentInfo payresult(@PathVariable String paymentId) throws IOException {
        //查询支付结果
        return paymentsService.queryPayResult(paymentId);
    }

    // 因为要内网穿透还不太会，所以还没有进行测试，不过感觉问题应该不大，后续继续改进
    @ApiOperation("支付完成回调,更新数据")
    @PostMapping("/callback")
    public CallbackResponse payCallback(HttpServletRequest request) throws Exception{
        return paymentsService.payCallback(request);
    }

}
