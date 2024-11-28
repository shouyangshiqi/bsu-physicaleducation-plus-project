package com.physicaleducation.content;

import com.physicaleducation.content.service.impl.CoursePublishServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author khran
 * @version 1.0
 * @description
 */
@SpringBootTest
public class CoursePublishServiceImplTest {

    @Autowired
    private CoursePublishServiceImpl coursePublishService;

    @Test
    public void saveCoursePublishMessageAdd(){
        coursePublishService.saveCoursePublishMessage(124L);
    }
}
