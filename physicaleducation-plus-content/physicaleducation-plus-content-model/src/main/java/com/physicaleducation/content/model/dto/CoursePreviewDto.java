package com.physicaleducation.content.model.dto;

import com.physicaleducation.content.model.po.CourseTeacher;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author khran
 * @version 1.0
 * @description
 */

@Data
@ToString
public class CoursePreviewDto {
    //课程基本信息,课程营销信息
    CourseBaseInfoDto courseBase;


    //课程计划信息
    List<TeachplanDto> teachplans;

    //师资信息
    List<CourseTeacher> courseTeacher;

}
