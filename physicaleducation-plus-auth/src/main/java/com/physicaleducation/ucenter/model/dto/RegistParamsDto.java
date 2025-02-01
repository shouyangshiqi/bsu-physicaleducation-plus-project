package com.physicaleducation.ucenter.model.dto;

import lombok.Data;

/**
 * @author khran
 * @version 1.0
 * @description
 */
@Data
public class RegistParamsDto extends FindPasswordParamsDto{

    // 用户名
    private String username;
    // 昵称
    private String nickname;

}
