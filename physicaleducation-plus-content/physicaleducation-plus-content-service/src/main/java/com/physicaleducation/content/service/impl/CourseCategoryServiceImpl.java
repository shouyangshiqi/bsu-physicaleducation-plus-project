package com.physicaleducation.content.service.impl;

import com.physicaleducation.content.mapper.CourseCategoryMapper;
import com.physicaleducation.content.model.dto.CourseCategoryTreeDto;
import com.physicaleducation.content.model.dto.QueryCourseParamsDto;
import com.physicaleducation.content.model.po.CourseBase;
import com.physicaleducation.content.model.po.CourseCategory;
import com.physicaleducation.content.service.CourseBaseInfoService;
import com.physicaleducation.content.service.CourseCategoryService;
import com.physicaleducation.model.PageParams;
import com.physicaleducation.model.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author khran
 * @version 1.0
 * @description
 */
@Slf4j
@Service
public class CourseCategoryServiceImpl implements CourseCategoryService {

    @Autowired
    private CourseCategoryMapper courseCategoryMapper;

    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes(String id) {

        ArrayList<CourseCategoryTreeDto> courseCategoryTreeDtos = new ArrayList<>();
        List<CourseCategory> courseCategories = courseCategoryMapper.selectTreeNodes(id);
        HashMap<String, List<CourseCategory>> map = new HashMap<>();

        // 将所有数据放入map结构中
        for(CourseCategory c1 : courseCategories){
            if(map.get(c1.getParentid()) == null){
                map.put(c1.getParentid(), new ArrayList<CourseCategory>());
            }
            map.get(c1.getParentid()).add(c1);
        }

        // 处理数据
        // 获取当前id 的子节点
        List<CourseCategory> courseCategories1 = map.get(id);
        for(CourseCategory courseCategory : courseCategories1){
            CourseCategoryTreeDto courseCategoryTreeDto = new CourseCategoryTreeDto();
            BeanUtils.copyProperties(courseCategory, courseCategoryTreeDto);
            // 获取子节点的子节点
            List<CourseCategory> courseCategories2 = map.get(courseCategoryTreeDto.getId());
            if(courseCategories2 != null) {
                for (CourseCategory courseCategory1 : courseCategories2) {
                    if (courseCategoryTreeDto.getChildrenTreeNodes() == null) {
                        courseCategoryTreeDto.setChildrenTreeNodes(new ArrayList<CourseCategory>());
                    }
                    courseCategoryTreeDto.getChildrenTreeNodes().add(courseCategory1);
                }
            }
            courseCategoryTreeDtos.add(courseCategoryTreeDto);
        }

        return courseCategoryTreeDtos;
    }
}
