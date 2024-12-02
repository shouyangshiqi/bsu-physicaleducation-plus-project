package com.physicaleducation.content.api;


import com.physicaleducation.content.model.dto.AddCourseDto;
import com.physicaleducation.content.model.dto.CourseBaseInfoDto;
import com.physicaleducation.content.model.dto.EditCourseDto;
import com.physicaleducation.content.model.dto.QueryCourseParamsDto;
import com.physicaleducation.content.model.po.CourseBase;
import com.physicaleducation.content.service.CourseBaseInfoService;
import com.physicaleducation.content.util.SecurityUtil;
import com.physicaleducation.model.PageParams;
import com.physicaleducation.model.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @ApiOperation("课程分页查询接口")
    @PreAuthorize("hasAuthority('xc_teachmanager_course_list')")
    @PostMapping("/course/list")
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody(required = false) QueryCourseParamsDto queryCourseParamsDto){
        PageResult<CourseBase> pageResult = courseBaseInfoService.queryCourseBaseList(pageParams, queryCourseParamsDto);
        return pageResult;
    }

    @ApiOperation("新增课程基础信息")
    @PostMapping("/course")
    public CourseBaseInfoDto createCourseBase(@RequestBody @Validated AddCourseDto addCourseDto){

        // 先将id写死
        Long companyId = 1232141425L;

        // 调用方法执行
        CourseBaseInfoDto courseBaseInfoDto = courseBaseInfoService.createCourseBase(companyId,addCourseDto);
        return courseBaseInfoDto;
    }

    @ApiOperation("课程id查询接口")
    @GetMapping("/course/{coursId}")
    public CourseBaseInfoDto getCourseBaseById(@PathVariable Long coursId){
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        System.out.println(user.getUsername());
        CourseBaseInfoDto courseBaseInfoDto = courseBaseInfoService.getCourseBaseById(coursId);
        return courseBaseInfoDto;
    }


    @ApiOperation("修改课程接口")
    @PutMapping("/course")
    public CourseBaseInfoDto updateCourseBase(@RequestBody @Validated EditCourseDto dto ){

        Long companyId = 1232141425L;

        CourseBaseInfoDto courseBaseInfoDto = courseBaseInfoService.updateCourseBase(dto, companyId);
        return  courseBaseInfoDto;
    }

    @ApiOperation("删除课程")
    @DeleteMapping("/course/{courseId}")
    public void deleteCourseBase(@PathVariable  Long courseId ){
        Long companyId = 1232141425L;
        courseBaseInfoService.deleteCourseBase(courseId, companyId);
    }

}
