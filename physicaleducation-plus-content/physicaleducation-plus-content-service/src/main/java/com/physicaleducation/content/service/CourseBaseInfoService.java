package com.physicaleducation.content.service;

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
}
