package com.physicaleducation.content;

import com.physicaleducation.content.mapper.CourseCategoryMapper;
import com.physicaleducation.content.model.dto.QueryCourseParamsDto;
import com.physicaleducation.content.model.po.CourseBase;
import com.physicaleducation.content.model.po.CourseCategory;
import com.physicaleducation.content.service.CourseCategoryService;
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
public class CourseCategoryServiceImplTests {
    @Autowired
    private CourseCategoryService courseCategoryService;

    @Autowired
    private CourseCategoryMapper courseCategoryMapper;
    @Test
    public void testCourseCategoryService(){
       courseCategoryService.queryTreeNodes("1");
    }

    @Test
    public void testSql(){
        courseCategoryMapper.selectTreeNodes("1");
    }

}
