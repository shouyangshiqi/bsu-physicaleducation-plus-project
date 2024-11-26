package com.physicaleducation.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.physicaleducation.content.mapper.TeachplanMapper;
import com.physicaleducation.content.mapper.TeachplanMediaMapper;
import com.physicaleducation.content.model.dto.BindTeachplanMediaDto;
import com.physicaleducation.content.model.dto.SaveTeachplanDto;
import com.physicaleducation.content.model.dto.TeachplanDto;
import com.physicaleducation.content.model.po.Teachplan;
import com.physicaleducation.content.model.po.TeachplanMedia;
import com.physicaleducation.content.service.TeachplanService;
import com.physicaleducation.execption.PEPlusException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    @Autowired
    private TeachplanMediaMapper teachplanMediaMapper;


    @Override
    public List<TeachplanDto> findTeachplanTree(long courseId) {
        List<TeachplanDto> teachplanDtos = teachplanMapper.selectTreeNodes(courseId);
        return teachplanDtos;
    }

    @Transactional
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
            int count = 0;
            try {
                count = teachplanMapper.selectMaxOrderby(teachplanDto.getCourseId(), teachplanDto.getParentid());
            } catch (Exception e) {
                count = 0;
            }

            Teachplan teachplan = new Teachplan();
            BeanUtils.copyProperties(teachplanDto, teachplan);
            teachplan.setOrderby(count + 1);
            teachplanMapper.insert(teachplan);
        }
    }

    @Transactional
    @Override
    public void deleteTeachplan(Long teachplanId) {
        int i = teachplanMapper.deleteById(teachplanId);
        if(i == 0){
            PEPlusException.cast("删除失败");
        }
    }

    @Transactional
    @Override
    public void movedownTeachplan(Long teachplanId) {
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        int orderBy = teachplan.getOrderby();
        // 查找出orderby+1的数据
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getOrderby, orderBy+1);
        queryWrapper.eq(Teachplan::getParentid, teachplan.getParentid());
        queryWrapper.eq(Teachplan::getCourseId, teachplan.getCourseId());
        Teachplan teachplanNext = teachplanMapper.selectOne(queryWrapper);
        if(teachplanNext == null){
            return;
        }
        teachplan.setOrderby(teachplanNext.getOrderby());
        teachplanNext.setOrderby(orderBy);

        // 更新原先数据
        teachplanMapper.updateById(teachplan);
        teachplanMapper.updateById(teachplanNext);
    }

    @Override
    public void moveupTeachplan(Long teachplanId) {
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        int orderBy = teachplan.getOrderby();
        // 查找出orderby+1的数据
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getOrderby, orderBy-1);
        queryWrapper.eq(Teachplan::getParentid, teachplan.getParentid());
        queryWrapper.eq(Teachplan::getCourseId, teachplan.getCourseId());
        Teachplan teachplanBefore = teachplanMapper.selectOne(queryWrapper);
        if(teachplanBefore == null){
            return;
        }
        teachplan.setOrderby(teachplanBefore.getOrderby());
        teachplanBefore.setOrderby(orderBy);

        // 更新原先数据
        teachplanMapper.updateById(teachplan);
        teachplanMapper.updateById(teachplanBefore);
    }

    @Override
    public TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto) {

        // 获取计划信息
        Teachplan teachplan = teachplanMapper.selectById(bindTeachplanMediaDto.getTeachplanId());
        if(teachplan == null){
            PEPlusException.cast("教学计划不存在");
        }

        // 如果有视频数据就先删除
        TeachplanMedia teachplanMedia1 = teachplanMediaMapper.selectOne(new LambdaQueryWrapper<TeachplanMedia>()
                .eq(TeachplanMedia::getTeachplanId, bindTeachplanMediaDto.getTeachplanId()));
        if(teachplanMedia1 != null){
            teachplanMediaMapper.deleteById(teachplanMedia1);
        }

        TeachplanMedia teachplanMedia = new TeachplanMedia();
        BeanUtils.copyProperties(bindTeachplanMediaDto, teachplanMedia);
        teachplanMedia.setCourseId(teachplan.getCourseId());
        teachplanMedia.setMediaFilename(bindTeachplanMediaDto.getFileName());
        teachplanMedia.setCreateDate(LocalDateTime.now());

        int insert = teachplanMediaMapper.insert(teachplanMedia);
        return teachplanMedia;

    }

/*  (淘汰该解法）求解满足条件的数据条数，然后以数据数量+1作为新增数据的orderby
 private int getTeachplanCount(Long courseId, Long parentId){
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId, courseId);
        queryWrapper.eq(Teachplan::getParentid, parentId);
        Integer count = teachplanMapper.selectCount(queryWrapper);
        return count+1;
    }*/
}
