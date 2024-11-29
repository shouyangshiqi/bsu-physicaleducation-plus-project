package com.physicaleducation.content.feignclient;

import com.physicaleducation.content.model.po.CourseIndex;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @author khran
 * @version 1.0
 * @description
 */

@Component
@Slf4j
public class SearchServiceClientFallbackFactory implements FallbackFactory<SearchServiceClient> {
    @Override
    public SearchServiceClient create(Throwable throwable) {
        return new SearchServiceClient() {
            @Override
            public Boolean add(CourseIndex courseIndex) {
                log.error("调用搜索发生熔断走降级方法,熔断异常:",throwable.getMessage());
                return null;
            }
        };
    }
}
