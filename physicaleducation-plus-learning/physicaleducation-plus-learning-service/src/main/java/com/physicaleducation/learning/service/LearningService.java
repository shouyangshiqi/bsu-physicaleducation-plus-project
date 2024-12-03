package com.physicaleducation.learning.service;

import com.physicaleducation.model.RestResponse;

/**
 * @author khran
 * @version 1.0
 * @description
 */

public interface LearningService {
    /**
     * 获取教学视频
     * @param userId 用户id
     * @param courseId 课程id
     * @param teachplanId 课程计划id
     * @param mediaId 媒资文件id
     * @return
     */
    public RestResponse<String> getVideo(String userId, Long courseId, Long teachplanId, String mediaId);
}
