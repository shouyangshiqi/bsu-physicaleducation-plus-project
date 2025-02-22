package com.physicaleducation.learning.api;

import com.physicaleducation.learning.service.LearningService;
import com.physicaleducation.learning.util.SecurityUtil;
import com.physicaleducation.model.RestResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Mr.M
 * @version 1.0
 * @description 我的学习接口
 * @date 2022/10/27 8:59
 */
@Api(value = "学习过程管理接口", tags = "学习过程管理接口")
@Slf4j
@RestController
public class MyLearningController {

    @Autowired
    private LearningService learningService;


    @ApiOperation("获取视频")
    @GetMapping("/open/learn/getvideo/{courseId}/{teachplanId}/{mediaId}")
    public RestResponse<String> getvideo(@PathVariable("courseId") Long courseId, @PathVariable("teachplanId") Long teachplanId, @PathVariable("mediaId") String mediaId) {
        // 获取用户信息
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        String userId = null;
        if(user != null){
            userId = user.getId();
        }
        // 获取视频信息
        //获取视频
        return learningService.getVideo(userId, courseId, teachplanId, mediaId);
    }

}
