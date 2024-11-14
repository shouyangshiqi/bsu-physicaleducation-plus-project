package com.physicaleducation.content.api;

import com.physicaleducation.content.model.dto.CourseCategoryTreeDto;
import com.physicaleducation.content.service.CourseCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author khran
 * @version 1.0
 * @description
 */

@Slf4j
@RestController
public class CourseCategoryController {
    @Autowired
    private CourseCategoryService courseCategoryService;
    @GetMapping("/course-category/tree-nodes")
    public List<CourseCategoryTreeDto> queryTreeNode(){
        return courseCategoryService.queryTreeNodes("1");
    }
}
