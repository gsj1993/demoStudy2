package com.example.demoStudy2;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({"com.example.*"})
@MapperScan(value = {"com.example.demoStudy2.dao"}, annotationClass = Mapper.class)
@SpringBootApplication
public class DemoStudyApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoStudyApplication.class, args);
    }
}
