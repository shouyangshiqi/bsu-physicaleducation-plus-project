package com.physicaleducation.content.service;

import com.physicaleducation.content.model.dto.AddCourseDto;
import com.physicaleducation.content.model.dto.CourseBaseInfoDto;
import com.physicaleducation.content.model.dto.EditCourseDto;
import com.physicaleducation.content.model.dto.QueryCourseParamsDto;
import com.physicaleducation.content.model.po.CourseBase;
import com.physicaleducation.content.service.impl.CourseBaseInfoServiceImpl;
import com.physicaleducation.model.PageParams;
import com.physicaleducation.model.PageResult;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author khran
 * @version 1.0
 * @description
 */

public interface CourseBaseInfoService {
    /**
     * 根据查询条件查询课程
     * @param pageParams 分页参数
     * @param queryCourseParamsDto 查询条件
     * @return 返回课程结果
     */
    PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto);

    /**
     *添加课程信息
     * @param companyId 用户所属机构id
     * @param addCourseDto 课程基本信息
     * @return
     */
    CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto);

    /**
     * 根据课程id查询课程信息
     * @param coursId
     * @return
     */
    CourseBaseInfoDto getCourseBaseById(Long coursId);

    CourseBaseInfoDto updateCourseBase(EditCourseDto editCourseDto, Long companyId);
}
