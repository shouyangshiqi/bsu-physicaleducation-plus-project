package com.dyds.orders.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author khran
 * @version 1.0
 * @description
 */

@Configuration
@MapperScan("com.dyds.orders.mapper")
public class MybatisPlusConfig {
}
