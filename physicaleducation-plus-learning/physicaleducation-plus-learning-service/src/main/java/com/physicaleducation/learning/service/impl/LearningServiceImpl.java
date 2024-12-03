package com.physicaleducation.learning.service.impl;

import com.physicaleducation.content.model.po.CoursePublish;
import com.physicaleducation.learning.feignclient.ContentServiceClient;
import com.physicaleducation.learning.feignclient.MediaServiceClient;
import com.physicaleducation.learning.model.dto.XcCourseTablesDto;
import com.physicaleducation.learning.service.LearningService;
import com.physicaleducation.learning.service.MyCourseTablesService;
import com.physicaleducation.model.RestResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author khran
 * @version 1.0
 * @description
 */
@Service
@Slf4j
public class LearningServiceImpl implements LearningService {

    @Autowired
    private ContentServiceClient contentServiceClient;

    @Autowired
    private MyCourseTablesService myCourseTablesService;

    @Autowired
    private MediaServiceClient mediaServiceClient;

    @Override
    public RestResponse<String> getVideo(String userId, Long courseId, Long teachplanId, String mediaId) {
        // 先查询课程信息是否存在
        CoursePublish coursepublish = contentServiceClient.getCoursepublish(courseId);
        if(coursepublish == null){
            return RestResponse.validfail("课程不存在");
        }

        // 用户是否登录
        if(StringUtils.isNoneEmpty(userId)){
            // 已登录
            // 查询是否有学习资格
            XcCourseTablesDto learningStatus = myCourseTablesService.getLearningStatus(userId, courseId);
            String learnStatus = learningStatus.getLearnStatus();
            if("702001".equals(learnStatus)){
                return mediaServiceClient.getPlayUrlByMediaId(mediaId);
            } else if (learnStatus.equals("702002")) {
                return RestResponse.validfail("无法观看，由于没有选课或选课后没有支付");
            } else if (learnStatus.equals("702003")) {
                return RestResponse.validfail("您的选课已过期需要申请续期或重新支付");
            }
        }

        // 未登录
        String charge = coursepublish.getCharge();
        if (charge.equals("201000")) {//免费可以正常学习
            return mediaServiceClient.getPlayUrlByMediaId(mediaId);
        }

        return RestResponse.validfail("请购买课程后继续学习");
    }
}
