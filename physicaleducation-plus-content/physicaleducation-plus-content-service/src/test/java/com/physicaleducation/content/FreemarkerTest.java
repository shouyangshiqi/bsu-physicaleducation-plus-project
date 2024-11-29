package com.physicaleducation.content;


import com.physicaleducation.content.model.dto.CoursePreviewDto;
import com.physicaleducation.content.service.CoursePublishService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;


/**
 * @author khran
 * @version 1.0
 * @description
 */
@SpringBootTest
public class FreemarkerTest {

    @Autowired
    private CoursePublishService coursePublishService;

    @Test
    public void testGenerateHtmlByTemplate() throws IOException, TemplateException {

        Configuration configuration = new Configuration(Configuration.getVersion());

        // 得到classpath
        String path = this.getClass().getResource("/").getPath();
        //指定模板的目录
        configuration.setDirectoryForTemplateLoading(new File(path+"/templates"));
        //指定编码
        configuration.setDefaultEncoding("utf-8");
        // 得到模板
        Template template = configuration.getTemplate("course_template.ftl");


        // 准备数据
        Long courseId = 124L;
        CoursePreviewDto coursePreviewDto = coursePublishService.getCoursePreviewInfo(courseId);

        HashMap<String, Object> map = new HashMap<>();
        map.put("model", coursePreviewDto);

        // 生成静态页面
        String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);

        InputStream inputStream = IOUtils.toInputStream(html);
        FileOutputStream fileOutputStream = new FileOutputStream("D:\\java_learn\\idea-program\\html-file\\test.html");
        // 输入流
        IOUtils.copy(inputStream, fileOutputStream);
    }

}
