package com.physicaleducation.ucenter.service;

import com.physicaleducation.ucenter.model.po.XcUser;

/**
 * @author khran
 * @version 1.0
 * @description
 */

public interface WxAuthService {

    /**
     * 微信的服务认证功能
     * @param code
     * @return
     */
    public XcUser wxAuth(String code);
}

