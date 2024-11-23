package com.physicaleducation.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.physicaleducation.content.model.dto.TeachplanDto;
import com.physicaleducation.content.model.po.Teachplan;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 课程计划 Mapper 接口
 * </p>
 *
 * @author khran
 */
public interface TeachplanMapper extends BaseMapper<Teachplan> {

    /**
     * 查询课程的课程计划，组成树形机构
     * @param
     * @return
     */
    public List<TeachplanDto> selectTreeNodes(Long courseId);

    public Integer selectMaxOrderby(@Param("courseId") Long courseId, @Param("parentId") Long parentId);

}
