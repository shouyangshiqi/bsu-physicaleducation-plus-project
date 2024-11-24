package com.physicaleducation.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.physicaleducation.content.mapper.CourseTeacherMapper;
import com.physicaleducation.content.model.po.CourseTeacher;
import com.physicaleducation.content.service.CourseTeacherInfoService;
import com.physicaleducation.execption.PEPlusException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author khran
 * @version 1.0
 * @description
 */

@Service
public class CourseTeacherInfoServiceImpl implements CourseTeacherInfoService {
    @Autowired
    private CourseTeacherMapper courseTeacherMapper;
    @Override
    public List<CourseTeacher> queryTeachers(Long courseId) {

        // 查询条件
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getCourseId, courseId);

        List<CourseTeacher> courseTeachers = courseTeacherMapper.selectList(queryWrapper);
        return courseTeachers;
    }

    @Transactional
    @Override
    public CourseTeacher addTeacher(CourseTeacher courseTeacher) {
        // 判断数据合法性
        if(courseTeacher.getCourseId() == null){
            PEPlusException.cast("课程id不能为空");
        }
        if(courseTeacher.getTeacherName() == null){
            PEPlusException.cast("教师名字不能为空");
        }
        if(courseTeacher.getPosition() == null){
            PEPlusException.cast("教师职位不能为空");
        }
        if(courseTeacher.getIntroduction() == null){
            PEPlusException.cast("老师简介不能为空");
        }

        // 执行添加逻辑
        // 判断是添加还是修改
        Long teacherId = courseTeacher.getId();
        if(teacherId == null){
            // 添加教师
            courseTeacher.setCreateDate(LocalDateTime.now());

            int rows = courseTeacherMapper.insert(courseTeacher);
            if(rows <= 0){
                PEPlusException.cast("数据插入失败");
            }
            return courseTeacher;
        }else {
            // 修改教师
            CourseTeacher courseTeacherOld = courseTeacherMapper.selectById(teacherId);
            LocalDateTime createDate = courseTeacherOld.getCreateDate();
            BeanUtils.copyProperties(courseTeacher, courseTeacherOld);
            courseTeacherOld.setCreateDate(createDate);
            courseTeacherMapper.updateById(courseTeacherOld);
            return courseTeacherOld;
        }
    }

    @Override
    public void deleteTeacher(Long courseId, Long teacherId) {
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getId, teacherId);
        queryWrapper.eq(CourseTeacher::getCourseId, courseId);
        int delete = courseTeacherMapper.delete(queryWrapper);
        if (delete <= 0) {
            PEPlusException.cast("删除失败");
        }
    }
}
