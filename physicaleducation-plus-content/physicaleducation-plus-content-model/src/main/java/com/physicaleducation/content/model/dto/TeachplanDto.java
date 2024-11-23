package com.physicaleducation.content.model.dto;

import com.physicaleducation.content.model.po.Teachplan;
import com.physicaleducation.content.model.po.TeachplanMedia;
import lombok.Data;

import java.util.List;

/**
 * @author khran
 * @version 1.0
 * @description
 */
@Data
public class TeachplanDto extends Teachplan {
    // 课程相关联的媒资信息
    private TeachplanMedia teachplanMedia;

    // 子节点
    private List<TeachplanDto> teachPlanTreeNodes;
}
