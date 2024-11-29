package com.physicaleducation.content.feignclient;

import com.physicaleducation.content.config.MultipartSupportConfig;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author khran
 * @version 1.0
 * @description
 *
 * 熔断后有两种方法进行处理，
 *  第一种是利用Fallback直接重写方法，发生熔断走这个重写的本地方法（拿不到熔断失败的原因）
 *  第二种是利用FallbackFactory工厂方法去实现新的方法，返回new的Feign类，可以拿到熔断失败信息
 */
@FeignClient(value = "media-api",configuration = MultipartSupportConfig.class, fallbackFactory = MediaServiceClientFallbackFactory.class)
public interface MediaServiceClient {

    @RequestMapping(value = "/media/upload/coursefile",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String uploadFile(@RequestPart("filedata") MultipartFile upload,@RequestParam(value = "objectName",required=false) String objectName);

}
