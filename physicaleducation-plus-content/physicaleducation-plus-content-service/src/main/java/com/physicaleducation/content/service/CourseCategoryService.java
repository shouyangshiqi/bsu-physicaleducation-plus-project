package com.physicaleducation.content.service;

import com.physicaleducation.content.model.dto.CourseCategoryTreeDto;

import java.util.List;

/**
 * @author khran
 * @version 1.0
 * @description
 */

public interface CourseCategoryService {

    /**
     *
     * @param id
     * @return
     */
    public List<CourseCategoryTreeDto> queryTreeNodes(String id);
}
