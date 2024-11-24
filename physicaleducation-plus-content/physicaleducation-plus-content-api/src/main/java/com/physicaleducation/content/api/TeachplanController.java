package com.physicaleducation.content.api;

import com.physicaleducation.content.model.dto.SaveTeachplanDto;
import com.physicaleducation.content.model.dto.TeachplanDto;
import com.physicaleducation.content.service.TeachplanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author khran
 * @version 1.0
 * @description
 */

@RestController
@Api(value = "课程计划编辑接口",tags = "课程计划编辑接口")
public class TeachplanController {

    @Autowired
    private TeachplanService teachplanService;

    @ApiOperation("查询课程计划树形结构")
    @ApiImplicitParam(value = "courseId",name = "课程基础Id值",required = true,dataType = "Long",paramType = "path")
    @GetMapping("/teachplan/{courseId}/tree-nodes")
    public List<TeachplanDto> getTreeNodes(@PathVariable Long courseId){
        List<TeachplanDto> result = teachplanService.findTeachplanTree(courseId);
        return result;
    }

    @ApiOperation("课程计划创建或修改")
    @PostMapping("/teachplan")
    public void saveTeachplan( @RequestBody SaveTeachplanDto teachplan){
        teachplanService.saveTeachplan(teachplan);
    }
    @ApiOperation("课程计划删除")
    @DeleteMapping("/teachplan/{id}")
    public void deleteTeachplan( @PathVariable Long id){
        teachplanService.deleteTeachplan(id);
    }


    @ApiOperation("课程计划下移")
    @PostMapping("/teachplan/movedown/{id}")
    public void movedownTeachplan( @PathVariable Long id){
        teachplanService.movedownTeachplan(id);
    }
    @ApiOperation("课程计划上移")
    @PostMapping("/teachplan/moveup/{id}")
    public void moveupTeachplan( @PathVariable Long id){
        teachplanService.moveupTeachplan(id);
    }
}
