package com.winit.graph;


import com.winit.graph.config.DruidDataSourceConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * 禁止自动装配连接池 exclude = DataSourceAutoConfiguration.class
 * 多个模块需要加上 scanBasePackages
 *
 * @Author zeyu.lin  00018326
 * @Date 15:33 2022/5/23
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class, scanBasePackages = {"com.winit"})
@MapperScan(basePackages = {"com.winit.graph.mapper", "com.winit.graphgen.mapper"})
@ComponentScan("com.winit")
@Import(DruidDataSourceConfig.class)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
