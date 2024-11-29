package com.physicaleducation.content;

import com.physicaleducation.content.mapper.CourseCategoryMapper;
import com.physicaleducation.content.service.CourseCategoryService;
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
