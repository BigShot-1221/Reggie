package com.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理
 */
//只要加RestController或者Controller注解的就会被捕获
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

  /**
   * 异常处理方法
   * 只要出现SQLIntegrityConstraintViolationException都会被捕获(这个异常是账号唯一,但是重复添加唯一账号异常)
   * @param e
   * @return
   */
  @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
  public R<String> exceptionHandler(SQLIntegrityConstraintViolationException e) {
    log.error(e.getMessage());

    //错误信息中包含这个文字的话
    if (e.getMessage().contains("Duplicate entry")){
      //错误信息为 Duplicate entry 'zhangsan' for key 'idx_username'
      //将错误信息分割开,拿到重复的username并声明
      String[] split = e.getMessage().split(" ");
      String msg = split[2] + "已存在";
      return R.error(msg);
    }

    return R.error("未知错误");
  }

  @ExceptionHandler(CustomException.class)
  public R<String> exceptionHandler(CustomException e) {
    log.error(e.getMessage());

    return R.error(e.getMessage());
  }
}
