package com.physicaleducation.content.api;


import com.physicaleducation.content.model.dto.QueryCourseParamsDto;
import com.physicaleducation.content.model.po.CourseBase;
import com.physicaleducation.model.PageParams;
import com.physicaleducation.model.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author khran
 * @version 1.0
 * @description  请求服务
 */
@Api(value = "课程信息编辑接口",tags = "课程信息编辑接口")
@RestController
public class CourseBaseInfoController {

    @ApiOperation("课程查询接口")
    @PostMapping("/course/list")
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody(required = false) QueryCourseParamsDto queryCourseParamsDto){
        return null;
    }
}
