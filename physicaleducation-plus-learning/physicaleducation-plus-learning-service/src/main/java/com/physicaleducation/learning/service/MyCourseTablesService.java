package com.physicaleducation.learning.service;

import com.physicaleducation.learning.model.dto.XcChooseCourseDto;
import com.physicaleducation.learning.model.dto.XcCourseTablesDto;

/**
 * @author khran
 * @version 1.0
 * @description
 */

public interface MyCourseTablesService {
    /**
     * @description 添加选课
     * @param userId 用户id
     * @param courseId 课程id
     * @return com.xuecheng.learning.model.dto.XcChooseCourseDto
     * @author Mr.M
     * @date 2022/10/24 17:33
     */
    public XcChooseCourseDto addChooseCourse(String userId, Long courseId);

    /**
     * 查询学习资格
     * @param userId
     * @param courseId
     * @return
     */
    public XcCourseTablesDto getLearningStatus(String userId, Long courseId);

}
