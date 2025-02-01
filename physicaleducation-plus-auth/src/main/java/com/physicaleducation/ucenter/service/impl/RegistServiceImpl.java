package com.physicaleducation.ucenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.physicaleducation.execption.PEPlusException;
import com.physicaleducation.model.RestResponse;
import com.physicaleducation.ucenter.feignclient.CheckcodeFeign;
import com.physicaleducation.ucenter.mapper.XcUserMapper;
import com.physicaleducation.ucenter.mapper.XcUserRoleMapper;
import com.physicaleducation.ucenter.model.dto.AuthParamsDto;
import com.physicaleducation.ucenter.model.dto.RegistParamsDto;
import com.physicaleducation.ucenter.model.dto.XcUserExt;
import com.physicaleducation.ucenter.model.po.XcUser;
import com.physicaleducation.ucenter.model.po.XcUserRole;
import com.physicaleducation.ucenter.service.AuthService;
import com.physicaleducation.ucenter.service.RegistService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author khran
 * @version 1.0
 * @description
 */
@Service
@Slf4j
public class RegistServiceImpl implements RegistService {

    @Autowired
    private CheckcodeFeign checkcodeFeign;

    @Autowired
    private XcUserMapper xcUserMapper;
    @Autowired
    private XcUserRoleMapper xcUserRoleMapper;

    @Autowired
    private RegistServiceImpl registServiceImpl;

    @Autowired
    private PasswordEncoder passwordEncode;

    @Override
    public RestResponse register(RegistParamsDto registParamsDto) {
        /* 取消校验验证码(短信还发送不了）
        Boolean flag = checkcodeFeign.verify(registParamsDto.getCheckcodeKey(), registParamsDto.getCheckcode());
        if(!flag){
            PEPlusException.cast("验证码错误");
        }*/
        // 校验密码
        String password = registParamsDto.getPassword();
        String confirmpwd = registParamsDto.getConfirmpwd();
        if(!password.equals(confirmpwd)){
            PEPlusException.cast("两次密码不相等");
        }
        // 验证码正确，查询用户
        XcUser xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getCellphone, registParamsDto.getCellphone()));
        if(xcUser != null){
            PEPlusException.cast("用户已存在");
        }
        // 验证码正确，查询用户
        xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getUsername, registParamsDto.getUsername()));
        if(xcUser != null){
            PEPlusException.cast("账号重复了，请重新修改账号");
        }
        xcUser = new XcUser();
        //验证完了就可以将数据插入用户表中
        BeanUtils.copyProperties(registParamsDto, xcUser);
        xcUser.setCreateTime(LocalDateTime.now());
        xcUser.setStatus("1");
        xcUser.setUtype("101001");
        xcUser.setName("学生"+ UUID.randomUUID());
        xcUser.setPassword(passwordEncode.encode(password));
        xcUserMapper.insert(xcUser);

        // 写入角色关系
        XcUserRole xcUserRole = new XcUserRole();
        xcUserRole.setUserId(xcUser.getId());
        xcUserRole.setRoleId("17");
        xcUserRole.setCreateTime(LocalDateTime.now());
        xcUserRoleMapper.insert(xcUserRole);

        return RestResponse.success(true, "注册成功");
    }

}
