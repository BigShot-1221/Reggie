package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.R;
import com.reggie.entity.Employee;
import com.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

  @Autowired
  private EmployeeService employeeService;

  /**
   * 员工登录
   *
   * @param request
   * @param employee
   * @return
   */
  @PostMapping("/login")
  public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {

    //1.讲页面提交的密码password进行MD5加密处理
    String password = employee.getPassword();
    password = DigestUtils.md5DigestAsHex(password.getBytes());

    // 2.根据页面提交的用户名username查询数据库
    LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(Employee::getUsername, employee.getUsername());
    Employee emp = employeeService.getOne(queryWrapper);
    //3.如果没有查询到则返回登录失败结果
    if (emp == null) {
      return R.error("登录失败");
    }
    //4.密码比对,如果不一致则返回登录失败结果
    if (!password.equals(emp.getPassword())) {
      return R.error("登陆失败");
    }
    //5.查看员工状态,如果为已禁用状态,则返回员工已禁用结果
    if (emp.getStatus() == 0) {
      return R.error("账号已禁用");
    }

    //6.登录成功,讲员工ID存入session并返回登陆成功结果
    request.getSession().setAttribute("employee", emp.getId());
    return R.success(emp);
  }

  //员工退出
  @PostMapping("/logout")
  public R<String> logout(HttpServletRequest request) {
    //清理session中保存的当前登录ID
    request.getSession().removeAttribute("employee");
    return R.success("退出成功");
  }

  /**
   * 新增员工
   *
   * @param employee
   * @return
   */
  @PostMapping
  public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
    log.info("新增员工,员工信息:{}", employee.toString());
    //设置初始密码为123456,进行MD5加密
    employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//    employee.setCreateTime(LocalDateTime.now());
//    employee.setUpdateTime(LocalDateTime.now());

    Long id = (Long) request.getSession().getAttribute("employee");
//    employee.setCreateUser(id);
//    employee.setUpdateUser(id);
    employeeService.save(employee);

    return R.success("新增员工成功");
  }

  /**
   * 员工信息分页查询
   * @param page
   * @param pageSize
   * @param name
   * @return
   */
  @GetMapping("page")
  public R<Page> page(int page, int pageSize, String name) {
    log.info("page = {}, pageSize = {}, name = {}", page, pageSize, name);

    //构造分页构造器
    Page pageInfo = new Page<>(page, pageSize);
    //构造条件构造器
    LambdaQueryWrapper<Employee> queryWapper = new LambdaQueryWrapper<>();
    //添加一个过滤条件(name不为空的话,就将name添加进去)
    queryWapper.like(!StringUtils.isEmpty(name), Employee::getName, name);
    //添加排序条件
    queryWapper.orderByDesc(Employee::getUpdateTime);
    //执行查询
    employeeService.page(pageInfo, queryWapper);
    return R.success(pageInfo);
  }

  /**
   * 根据员工ID修改信息
   * @param employee
   * @return
   */
  @PutMapping
  public R<String> stop(HttpServletRequest request, @RequestBody Employee employee){
    long id = Thread.currentThread().getId();
    log.info("线程id为:{}", id);
    log.info(employee.toString());
    Long emplId = (Long) request.getSession().getAttribute("employee");
//    employee.setUpdateTime(LocalDateTime.now());
//    employee.setUpdateUser(emplId);
    boolean b = employeeService.updateById(employee);

    return R.success("修改员工信息成功");
  }

  /**
   * 根据ID查询员工ID
   * @param id
   * @return
   */
  @GetMapping("/{id}")
  public R<Employee> getById(@PathVariable Long id){
    log.info("根据员工查询ID");
    Employee employee = employeeService.getById(id);
    if (employee != null){
     return R.success(employee);
    }
    return R.error("没有查询到对应员工信息");
  }
}
