package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.reggie.common.R;
import com.reggie.entity.User;
import com.reggie.service.UserService;
import com.reggie.utils.SMSUtils;
import com.reggie.utils.ValidateCodeUtils;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private StringRedisTemplate redisTemplate;


    /**
     * 发送手机短信验证码
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    @Transactional
    public R<String> sendMsg(@RequestBody User user) {
        //获取手机号
        String phone = user.getPhone();

        if (StringUtil.isNullOrEmpty(phone)){
            return R.success("短信发送失败");
        }

        //生成随机4位验证码
        String code = ValidateCodeUtils.generateValidateCode(4).toString();
        log.info("验证码:{}", code);

        //调用阿里云短信api
        SMSUtils.sendMessage("阿里云短信测试", "SMS_154950909", phone, code);

        //需要将生成的验证码保存起来
        ValueOperations<String, String> forValue = redisTemplate.opsForValue();
        forValue.set(phone, code);

        return R.success("短信发送成功");
    }

    /**
     * 移动端用户登录
     * @param map
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpServletRequest request) {
        log.info("门户用户登录:{}", map.toString());
        //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();
        //从redis中获取到验证码
        ValueOperations<String, String> forValue = redisTemplate.opsForValue();
        String verifyCode = forValue.get(phone);
        //进行验证码比对
        if (verifyCode !=null && code.equals(verifyCode)){
            //如果对比成功说明登录成功
            //判读当前手机号对应的用户是否为新用户,如果是新用户自动完成注册
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(queryWrapper);
            if (user == null){
                user = new User();
                user.setPhone(phone);
                userService.save(user);
            }
            request.getSession().setAttribute("user", user.getId());
            return R.success(user);
        }
        return R.error("登录失败");
    }

}
