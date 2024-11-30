package com.physicaleducation.ucenter.service;

/**
 * @author khran
 * @version 1.0
 * @description
 */

import com.physicaleducation.ucenter.model.dto.AuthParamsDto;
import com.physicaleducation.ucenter.model.dto.XcUserExt;

/**
 * 认证service
 */
public interface AuthService {
    /**
     * 认证方法
     * @param authParamsDto
     * @return
     */
    XcUserExt execute(AuthParamsDto authParamsDto);
}
