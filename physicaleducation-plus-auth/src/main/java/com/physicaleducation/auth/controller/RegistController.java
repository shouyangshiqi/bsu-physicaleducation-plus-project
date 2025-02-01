package com.physicaleducation.auth.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.physicaleducation.model.RestResponse;
import com.physicaleducation.ucenter.model.dto.RegistParamsDto;
import com.physicaleducation.ucenter.service.RegistService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author khran
 * @version 1.0
 * @description
 */
@Controller

public class RegistController {
    @Autowired
    private RegistService registService;

    @ApiOperation("注册用户信息")
    @PostMapping("/register")
    public RestResponse regester(@RequestBody RegistParamsDto registParamsDto){
       return registService.register(registParamsDto);
    }
}
