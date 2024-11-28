package com.physicaleducation.content.service;

import com.physicaleducation.content.model.dto.CoursePreviewDto;

/**
 * @author khran
 * @version 1.0
 * @description
 */

public interface CoursePublishService{

    /**
     * @description 获取课程预览信息
     * @param courseId 课程id
     * @return com.xuecheng.content.model.dto.CoursePreviewDto
     * @author Mr.M
     * @date 2022/9/16 15:36
     */
    public CoursePreviewDto getCoursePreviewInfo(Long courseId);

    public void commitAudit(Long companyId, Long courseId);

    public void coursepublish(Long companyId, Long courseId);

}
