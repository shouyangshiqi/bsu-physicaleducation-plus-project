package com.physicaleducation.messagesdk.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * <P>
 * 		Mybatis-Plus 配置
 * </p>
 */
@Configuration("messagesdk_mpconfig")
@MapperScan("com.physicaleducation.messagesdk.mapper")
public class MybatisPlusConfig {

}