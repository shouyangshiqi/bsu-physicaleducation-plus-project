package com.physicaleducation.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.physicaleducation.ucenter.mapper.XcUserMapper;
import com.physicaleducation.ucenter.mapper.XcUserRoleMapper;
import com.physicaleducation.ucenter.model.dto.AuthParamsDto;
import com.physicaleducation.ucenter.model.dto.XcUserExt;
import com.physicaleducation.ucenter.model.po.XcUser;
import com.physicaleducation.ucenter.model.po.XcUserRole;
import com.physicaleducation.ucenter.service.AuthService;
import com.physicaleducation.ucenter.service.WxAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * @author khran
 * @version 1.0
 * @description
 */
@Service("wx_authservice")
@Slf4j
public class WxAuthServiceImpl implements AuthService, WxAuthService {

    @Autowired
    private XcUserMapper xcUserMapper;

    @Autowired
    XcUserRoleMapper xcUserRoleMapper;

    @Autowired
    private WxAuthServiceImpl wxAuthService;

    @Value("${weixin.appid}")
    String appid;
    @Value("${weixin.secret}")
    String secret;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {
        //账号
        String username = authParamsDto.getUsername();
        XcUser user = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getUsername, username));
        if(user==null){
            //返回空表示用户不存在
            throw new RuntimeException("账号不存在");
        }
        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(user,xcUserExt);
        return xcUserExt;
    }

    @Override
    public XcUser wxAuth(String code) {
        // 收到code调用微信接口申请access_token
        Map<String, String> access_token_map = getAccess_token(code);
        if(access_token_map==null){
            return null;
        }
        System.out.println(access_token_map);
        String openid = access_token_map.get("openid");
        String access_token = access_token_map.get("access_token");
        //拿access_token查询用户信息
        Map<String, String> userinfo = getUserinfo(access_token, openid);
        if(userinfo==null){
            return null;
        }
        //添加用户到数据库
        XcUser xcUser = wxAuthService.addWxUser(userinfo);

        return xcUser;
    }

    /**
     * 获取access_token
     * @param code
     * @return
     */

    // TODO: appid 和 secret 还不是真的，没有正式取得，所以还无法微信登录（但是流程是通顺的）
    private Map<String, String> getAccess_token(String code){
        String wxUrl_template = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
        //请求微信地址
        String wxUrl = String.format(wxUrl_template, appid, secret, code);

        log.info("调用微信接口申请access_token, url:{}", wxUrl);

        ResponseEntity<String> exchange = restTemplate.exchange(wxUrl, HttpMethod.POST, null, String.class);

        String result = exchange.getBody();
        log.info("调用微信接口申请access_token: 返回值:{}", result);
        Map<String,String> resultMap = JSON.parseObject(result, Map.class);

        return resultMap;
    }

    private Map<String,String> getUserinfo(String access_token,String openid) {

        String wxUrl_template = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s";
        //请求微信地址
        String wxUrl = String.format(wxUrl_template, access_token,openid);

        log.info("调用微信接口申请access_token, url:{}", wxUrl);

        ResponseEntity<String> exchange = restTemplate.exchange(wxUrl, HttpMethod.POST, null, String.class);

        //防止乱码进行转码
        String result = new     String(exchange.getBody().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        log.info("调用微信接口申请access_token: 返回值:{}", result);
        Map<String,String> resultMap = JSON.parseObject(result, Map.class);

        return resultMap;
    }

    @Transactional
    public XcUser addWxUser(Map userInfo_map){
        String unionid = userInfo_map.get("unionid").toString();
        XcUser xcUser = new XcUser();
        xcUser.setWxUnionid(unionid);
        //记录从微信得到的昵称
        xcUser.setNickname(userInfo_map.get("nickname").toString());
        xcUser.setUserpic(userInfo_map.get("headimgurl").toString());
        xcUser.setName(userInfo_map.get("nickname").toString());
        xcUser.setUsername(unionid);
        xcUser.setPassword(unionid);
        xcUser.setUtype("101001");//学生类型
        xcUser.setStatus("1");//用户状态
        xcUser.setCreateTime(LocalDateTime.now());
        // 查看原数据中是否存在用户数据,存在就更新数据
        XcUser xcUserOld = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getWxUnionid, unionid));
        if(xcUserOld != null){
            xcUser.setId(xcUserOld.getId());
            xcUserMapper.updateById(xcUser);
        }
        // 不存在就添加数据
        String userId = UUID.randomUUID().toString();
        xcUser.setId(userId);
        xcUserMapper.insert(xcUser);
        XcUserRole xcUserRole = new XcUserRole();
        xcUserRole.setId(UUID.randomUUID().toString());
        xcUserRole.setUserId(userId);
        xcUserRole.setRoleId("17");//学生角色
        xcUserRoleMapper.insert(xcUserRole);
        return xcUser;
    }
}
