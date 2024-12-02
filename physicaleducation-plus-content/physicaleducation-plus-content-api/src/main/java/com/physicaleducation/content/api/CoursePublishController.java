package com.physicaleducation.content.api;

import com.physicaleducation.content.model.dto.CoursePreviewDto;
import com.physicaleducation.content.model.po.CoursePublish;
import com.physicaleducation.content.service.CoursePublishService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author khran
 * @version 1.0
 * @description
 */

@Controller
public class CoursePublishController {
    @Autowired
    private CoursePublishService coursePublishService;

    @GetMapping("/coursepreview/{courseId}")
    public ModelAndView preview(@PathVariable("courseId") Long courseId){

        CoursePreviewDto coursePreviewDto = coursePublishService.getCoursePreviewInfo(courseId);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("model",coursePreviewDto);
        modelAndView.setViewName("course_template");
        return modelAndView;
    }

    @ResponseBody
    @PostMapping("/courseaudit/commit/{courseId}")
    public void commitAudit(@PathVariable("courseId") Long courseId){
        Long companyId = 1232141425L;
        coursePublishService.commitAudit(companyId, courseId);
    }

    @ApiOperation("课程发布")
    @ResponseBody
    @PostMapping ("/coursepublish/{courseId}")
    public void coursepublish(@PathVariable("courseId") Long courseId){
        Long companyId = 1232141425L;
        coursePublishService.coursepublish(companyId, courseId);
    }

    @ApiOperation("查询课程发布信息")
    @ResponseBody
    @GetMapping("/r/coursepublish/{courseId}")
    public CoursePublish getCoursepublish(@PathVariable("courseId") Long courseId) {
        CoursePublish coursePublish = coursePublishService.getCoursePublish(courseId);
        return coursePublish;
    }
}
