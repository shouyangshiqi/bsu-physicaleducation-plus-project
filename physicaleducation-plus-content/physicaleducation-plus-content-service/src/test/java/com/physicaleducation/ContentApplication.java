package com.physicaleducation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author khran
 * @version 1.0
 * @description
 */


@SpringBootApplication
@EnableFeignClients(basePackages={"com.physicaleducation.content.feignclient"})
public class ContentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ContentApplication.class, args);
    }
}
