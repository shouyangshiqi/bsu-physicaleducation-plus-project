package com.physicaleducation.ucenter.model.dto;

import lombok.Data;

/**
 * @author khran
 * @version 1.0
 * @description
 */
@Data
public class FindPasswordParamsDto {
    // 手机号
    private String cellphone;
    // 邮箱
    private String email;
    // 验证码key
    private String checkcodeKey;
    // 验证码
    private String checkcode;
    // 密码
    private String password;
    // 重复确认的密码
    private String confirmpwd;
}
