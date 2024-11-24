package com.physicaleducation.content.service;

import com.physicaleducation.content.model.po.CourseTeacher;

import java.util.List;

/**
 * @author khran
 * @version 1.0
 * @description
 */

public interface CourseTeacherInfoService {
    /**
     * 查询该课程的教师信息
     * @param courseId
     * @return
     */
    List<CourseTeacher> queryTeachers(Long courseId);

    /**
     * 添加教师信息
     * @param courseTeacher
     * @return
     */
    CourseTeacher addTeacher(CourseTeacher courseTeacher);

    /**
     * 删除教师信息
     * @param courseId
     * @param teacherId
     */
    void deleteTeacher(Long courseId, Long teacherId);
}
