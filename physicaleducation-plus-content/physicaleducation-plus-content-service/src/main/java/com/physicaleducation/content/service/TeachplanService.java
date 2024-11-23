package com.physicaleducation.content.service;

import com.physicaleducation.content.model.dto.SaveTeachplanDto;
import com.physicaleducation.content.model.dto.TeachplanDto;

import java.util.List;

/**
 * @author khran
 * @version 1.0
 * @description
 */

public interface TeachplanService {

    /**
     * 课程计划查询,树形结构
     * @param courseId
     * @return
     */
    public List<TeachplanDto> findTeachplanTree(long courseId);

    /**
     * 保存课程计划
     * @param teachplanDto
     */
    public void saveTeachplan(SaveTeachplanDto teachplanDto);
}
