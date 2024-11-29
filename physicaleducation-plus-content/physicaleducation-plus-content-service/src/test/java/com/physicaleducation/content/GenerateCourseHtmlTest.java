package com.physicaleducation.content;

import com.physicaleducation.content.service.jobhandler.CoursePublishTask;
import com.physicaleducation.messagesdk.mapper.MqMessageMapper;
import com.physicaleducation.messagesdk.model.po.MqMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author khran
 * @version 1.0
 * @description
 */

@SpringBootTest
public class GenerateCourseHtmlTest {
    @Autowired
    private CoursePublishTask coursePublishTask;

    @Autowired
    private MqMessageMapper mqMessageMapper;
    @Test
    public void testGenerate(){
        MqMessage mqMessage = mqMessageMapper.selectById(4L);
        coursePublishTask.generateCourseHtml(mqMessage, 124L);
    }
}
