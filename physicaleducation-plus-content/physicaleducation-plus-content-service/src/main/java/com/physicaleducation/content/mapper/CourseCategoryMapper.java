package com.physicaleducation.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.physicaleducation.content.model.dto.CourseCategoryTreeDto;
import com.physicaleducation.content.model.po.CourseCategory;

import java.util.List;

/**
 * <p>
 * 课程分类 Mapper 接口
 * </p>
 *
 * @author khran
 */
public interface CourseCategoryMapper extends BaseMapper<CourseCategory> {

    // 使用递归来查询分类
    public List<CourseCategory> selectTreeNodes(String id);

}
