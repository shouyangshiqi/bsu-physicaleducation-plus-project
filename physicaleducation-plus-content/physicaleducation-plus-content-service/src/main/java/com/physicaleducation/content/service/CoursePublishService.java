package com.physicaleducation.content.service;

import com.physicaleducation.content.model.dto.CoursePreviewDto;
import com.physicaleducation.content.model.po.CoursePublish;

import java.io.File;

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

    /**
     * 课程提交审核
     * @param companyId
     * @param courseId
     */
    public void commitAudit(Long companyId, Long courseId);

    /**
     * 课程发布
     * @param companyId
     * @param courseId
     */
    public void coursepublish(Long companyId, Long courseId);

    /**
     * @description 课程静态化
     * @param courseId  课程id
     * @return File 静态化文件
     * @author khran
     * @date 2022/9/23 16:59
     */
    public File generateCourseHtml(Long courseId);
    /**
     * @description 上传课程静态化页面
     * @param file  静态化文件
     * @return void
     * @author khran
     * @date 2022/9/23 16:59
     */
    public void  uploadCourseHtml(Long courseId,File file);

    /**
     * 查询发布的课程信息
     * @param courseId
     * @return
     */
    CoursePublish getCoursePublish(Long courseId);
}
