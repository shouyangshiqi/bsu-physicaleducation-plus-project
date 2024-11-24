package com.physicaleducation.content.api;

import com.physicaleducation.content.model.po.CourseTeacher;
import com.physicaleducation.content.service.CourseTeacherInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author khran
 * @version 1.0
 * @description
 */

@RestController
@Api(value = "教师信息接口",tags = "教师信息编辑接口")
public class CourseTeacherInfoController {

    @Autowired
    private CourseTeacherInfoService courseTeacherInfoService;

    @ApiOperation("查询教师接口")
    @GetMapping("/courseTeacher/list/{courseId}")
    public List<CourseTeacher> queryTeachers(@PathVariable Long courseId){
        return courseTeacherInfoService.queryTeachers(courseId);
    }

    @ApiOperation("添加或修改教师")
    @PostMapping("/courseTeacher")
    public CourseTeacher addTeacher(@RequestBody CourseTeacher courseTeacher){
        return courseTeacherInfoService.addTeacher(courseTeacher);
    }

    @ApiOperation("删除教师")
    @DeleteMapping("/courseTeacher/course/{courseId}/{teacherId}")
    public void deleteTeacher(@PathVariable Long courseId, @PathVariable Long teacherId){
        courseTeacherInfoService.deleteTeacher(courseId, teacherId);
    }
}
