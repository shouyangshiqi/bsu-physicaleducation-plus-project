package com.physicaleducation;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author khran
 * @version 1.0
 * @description 内容管理服务启动类
 */

@EnableSwagger2Doc
@SpringBootApplication
@EnableFeignClients(basePackages={"com.physicaleducation.content.feignclient"})
public class ContentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ContentApplication.class, args);
    }
}
