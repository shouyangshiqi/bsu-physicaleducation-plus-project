package com.physicaleducation.content;

import com.physicaleducation.content.model.dto.QueryCourseParamsDto;
import com.physicaleducation.content.model.po.CourseBase;
import com.physicaleducation.content.service.impl.CourseBaseInfoServiceImpl;
import com.physicaleducation.model.PageParams;
import com.physicaleducation.model.PageResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


/**
 * @author khran
 * @version 1.0
 * @description
 */

@SpringBootTest
public class CourseBaseInfoServiceImplTests {
    @Autowired
    private CourseBaseInfoServiceImpl courseBaseInfoServiceImpl;

    @Test
    public void testCourseBaseMapper(){
        //查询条件
        QueryCourseParamsDto queryCourseParamsDto = new QueryCourseParamsDto();
        queryCourseParamsDto.setCourseName("java");
        queryCourseParamsDto.setAuditStatus("202004");
        queryCourseParamsDto.setPublishStatus("203001");

        //分页参数
        PageParams pageParams = new PageParams();
        pageParams.setPageNo(1L);//页码
        pageParams.setPageSize(3L);//每页记录数

        PageResult<CourseBase> courseBasePageResult = courseBaseInfoServiceImpl.queryCourseBaseList(pageParams, queryCourseParamsDto);
        System.out.println("courseBasePageResult = " + courseBasePageResult);
    }

}
