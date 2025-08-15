package com.qiuzhitech.onlineshopping_07;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.qiuzhitech.onlineshopping_07.db.mappers")
@ComponentScan(basePackages = "com.qiuzhitech")
public class OnlineShopping07Application {


    public static void main(String[] args) {
        SpringApplication.run(OnlineShopping07Application.class, args);
    }

}
