package com.physicaleducation.content.feignclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author khran
 * @version 1.0
 * @description
 */

@Slf4j
public class MediaServiceClientFallback implements MediaServiceClient{
    @Override
    public String uploadFile(MultipartFile upload, String objectName) {
        // 发生熔断降级就会走这个方法
        log.error("media-api发生熔断降级");
        return null;
    }
}
