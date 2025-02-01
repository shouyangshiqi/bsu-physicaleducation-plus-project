package com.physicaleducation.auth.controller;

import com.physicaleducation.model.RestResponse;
import com.physicaleducation.ucenter.mapper.XcUserMapper;
import com.physicaleducation.ucenter.model.dto.RegistParamsDto;
import com.physicaleducation.ucenter.model.po.XcUser;
import com.physicaleducation.ucenter.service.AuthService;
import com.physicaleducation.ucenter.service.RegistService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author Mr.M
 * @version 1.0
 * @description 测试controller
 * @date 2022/9/27 17:25
 */
@Slf4j
@Controller
public class LoginController {

    @Autowired
    XcUserMapper userMapper;

    @Autowired()
    private RegistService registService;


    @RequestMapping("/login-success")
    public String loginSuccess() {

        return "登录成功";
    }


    @RequestMapping("/user/{id}")
    public XcUser getuser(@PathVariable("id") String id) {
        XcUser xcUser = userMapper.selectById(id);
        return xcUser;
    }

    @RequestMapping("/r/r1")
    @PreAuthorize("hasAuthority('p1')")//拥有p1权限方可访问
    public String r1() {
        return "访问r1资源";
    }

    @RequestMapping("/r/r2")
    @PreAuthorize("hasAuthority('p2')")//拥有p2权限方可访问
    public String r2() {
        return "访问r2资源";
    }



}
