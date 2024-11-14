package com.physicaleducation.content.model.dto;

import com.physicaleducation.content.model.po.CourseCategory;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author khran
 * @version 1.0
 * @description
 */

@Data
public class CourseCategoryTreeDto extends CourseCategory implements Serializable {

    private List<CourseCategory> childrenTreeNodes;
}
