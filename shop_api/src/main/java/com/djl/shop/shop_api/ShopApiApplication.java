package com.djl.shop.shop_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableDiscoveryClient // 开启注册发现
@EnableSwagger2 // 开启swagger在线api文档功能
@EnableFeignClients // 此注解的作用是扫描标记了@FeignClient的接口并创建实例bean
public class ShopApiApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(ShopApiApplication.class, args);
    }

    /**
     * 重写configure方法实现项目war运行
     * @param builder
     * @return
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(ShopApiApplication.class);
    }

}
