package com.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j  //lombok提供的，编写实体类时，get，set方法就可以省略了
@SpringBootApplication
@ServletComponentScan  //开启组件扫描
@EnableTransactionManagement   //开启事务控制功能
@EnableCaching  //开启注解缓存功能
public class ReggieApplication {
  public static void main(String[] ages) {
    SpringApplication.run(ReggieApplication.class, ages);
    log.info("项目启动成功");  //Slf4j的方法
  }


}
