package com.physicaleducation.content.service.impl;

import com.physicaleducation.content.mapper.CourseBaseMapper;
import com.physicaleducation.content.model.dto.CourseBaseInfoDto;
import com.physicaleducation.content.model.dto.CoursePreviewDto;
import com.physicaleducation.content.model.dto.TeachplanDto;
import com.physicaleducation.content.model.po.CourseBase;
import com.physicaleducation.content.model.po.CourseTeacher;
import com.physicaleducation.content.model.po.Teachplan;
import com.physicaleducation.content.service.CourseBaseInfoService;
import com.physicaleducation.content.service.CoursePublishService;
import com.physicaleducation.content.service.CourseTeacherInfoService;
import com.physicaleducation.content.service.TeachplanService;
import com.physicaleducation.execption.PEPlusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author khran
 * @version 1.0
 * @description
 */

@Service
public class CoursePublishServiceImpl implements CoursePublishService {

    @Autowired
    private CourseBaseInfoService courseBaseInfoService;

    @Autowired
    private TeachplanService teachplanService;

    @Autowired
    private CourseTeacherInfoService courseTeacherInfoService;
    @Override
    public CoursePreviewDto getCoursePreviewInfo(Long courseId) {
        // 获取课程基本信息
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseById(courseId);

        // 拷贝给数据
        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
        coursePreviewDto.setCourseBase(courseBaseInfo);

        // 获取课程计划
        List<TeachplanDto> teachplanTree = teachplanService.findTeachplanTree(courseId);
        coursePreviewDto.setTeachplans(teachplanTree);

        // 获取教师信息
        List<CourseTeacher> courseTeachers = courseTeacherInfoService.queryTeachers(courseId);
        coursePreviewDto.setCourseTeacher(courseTeachers);

        return coursePreviewDto;

    }
}
