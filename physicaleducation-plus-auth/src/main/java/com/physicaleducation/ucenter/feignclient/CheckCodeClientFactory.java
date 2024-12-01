package com.physicaleducation.ucenter.feignclient;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * @author khran
 * @version 1.0
 * @description
 */

@Slf4j
public class CheckCodeClientFactory implements FallbackFactory<CheckcodeFeign> {

    @Override
    public CheckcodeFeign create(Throwable throwable) {
        return new CheckcodeFeign() {
            @Override
            public Boolean verify(String key, String code) {
                log.error("调用验证发生熔断走降级方法,熔断异常:",throwable.getMessage());
                return null;
            }
        };
    }
}
