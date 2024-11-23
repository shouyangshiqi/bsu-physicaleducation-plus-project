package com.physicaleducation.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.physicaleducation.content.mapper.TeachplanMapper;
import com.physicaleducation.content.model.dto.SaveTeachplanDto;
import com.physicaleducation.content.model.dto.TeachplanDto;
import com.physicaleducation.content.model.po.Teachplan;
import com.physicaleducation.content.service.TeachplanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author khran
 * @version 1.0
 * @description
 */

@Service
public class TeachplanServiceImpl implements TeachplanService {

    @Autowired
    private TeachplanMapper teachplanMapper;

    @Override
    public List<TeachplanDto> findTeachplanTree(long courseId) {
        List<TeachplanDto> teachplanDtos = teachplanMapper.selectTreeNodes(courseId);
        return teachplanDtos;
    }

    @Override
    public void saveTeachplan(SaveTeachplanDto teachplanDto) {
        Long id = teachplanDto.getId();
        if(id != null){
            // 修改课程计划
            Teachplan teachplan = teachplanMapper.selectById(id);
            BeanUtils.copyProperties(teachplanDto, teachplan);
            teachplanMapper.updateById(teachplan);
        }else {
            // 新增课程计划
            int count = teachplanMapper.selectMaxOrderby(teachplanDto.getCourseId(), teachplanDto.getParentid());

            Teachplan teachplan = new Teachplan();
            BeanUtils.copyProperties(teachplanDto, teachplan);
            teachplan.setOrderby(count + 1);
            teachplanMapper.insert(teachplan);
        }
    }

/*  求解满足条件的数据条数，然后以数据数量+1作为新增数据的orderby
 private int getTeachplanCount(Long courseId, Long parentId){
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId, courseId);
        queryWrapper.eq(Teachplan::getParentid, parentId);
        Integer count = teachplanMapper.selectCount(queryWrapper);
        return count+1;
    }*/
}
