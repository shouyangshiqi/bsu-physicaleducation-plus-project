package com.physicaleducation.content.api;


import com.physicaleducation.content.model.dto.AddCourseDto;
import com.physicaleducation.content.model.dto.CourseBaseInfoDto;
import com.physicaleducation.content.model.dto.QueryCourseParamsDto;
import com.physicaleducation.content.model.po.CourseBase;
import com.physicaleducation.content.service.CourseBaseInfoService;
import com.physicaleducation.model.PageParams;
import com.physicaleducation.model.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author khran
 * @version 1.0
 * @description  请求服务
 */
@Api(value = "课程信息编辑接口",tags = "课程信息编辑接口")
@RestController
public class CourseBaseInfoController {
    @Autowired
    private CourseBaseInfoService courseBaseInfoService;

    @ApiOperation("课程查询接口")
    @PostMapping("/course/list")
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody(required = false) QueryCourseParamsDto queryCourseParamsDto){
        PageResult<CourseBase> pageResult = courseBaseInfoService.queryCourseBaseList(pageParams, queryCourseParamsDto);
        return pageResult;
    }

    @ApiOperation("新增课程基础信息")
    @PostMapping("/course")
    public CourseBaseInfoDto createCourseBase(@RequestBody AddCourseDto addCourseDto){

        // 先将id写死
        Long companyId = 1232141425L;

        // 调用方法执行
        CourseBaseInfoDto courseBaseInfoDto = courseBaseInfoService.createCourseBase(companyId,addCourseDto);
        return courseBaseInfoDto;
    }
}
