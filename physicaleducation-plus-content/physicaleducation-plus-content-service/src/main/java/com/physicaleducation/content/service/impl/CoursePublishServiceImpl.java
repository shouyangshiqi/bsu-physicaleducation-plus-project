package com.physicaleducation.content.service.impl;

import com.alibaba.fastjson.JSON;
import com.physicaleducation.content.mapper.*;
import com.physicaleducation.content.model.dto.CourseBaseInfoDto;
import com.physicaleducation.content.model.dto.CoursePreviewDto;
import com.physicaleducation.content.model.dto.TeachplanDto;
import com.physicaleducation.content.model.po.*;
import com.physicaleducation.content.service.CourseBaseInfoService;
import com.physicaleducation.content.service.CoursePublishService;
import com.physicaleducation.content.service.CourseTeacherInfoService;
import com.physicaleducation.content.service.TeachplanService;
import com.physicaleducation.execption.CommonError;
import com.physicaleducation.execption.PEPlusException;
import com.physicaleducation.messagesdk.model.po.MqMessage;
import com.physicaleducation.messagesdk.service.MqMessageService;
import com.physicaleducation.utils.JsonUtil;
import org.apache.commons.lang3.StringUtils;
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
public class CoursePublishServiceImpl implements CoursePublishService {

    @Autowired
    private CourseBaseInfoService courseBaseInfoService;

    @Autowired
    private TeachplanService teachplanService;

    @Autowired
    private CourseTeacherInfoService courseTeacherInfoService;

    @Autowired
    private CourseBaseMapper courseBaseMapper;

    @Autowired
    private CourseMarketMapper courseMarketMapper;

    @Autowired
    private CoursePublishPreMapper coursePublishPreMapper;

    @Autowired
    private CoursePublishMapper coursePublishMapper;

    @Autowired
    private MqMessageService mqMessageService;

    @Override
    public CoursePreviewDto getCoursePreviewInfo(Long courseId) {
        // 获取课程基本信息
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseById(courseId);

        // 拷贝给数据
        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
        coursePreviewDto.setCourseBase(courseBaseInfo);

        // 获取课程计划
        List<TeachplanDto> teachplanTree = teachplanService.findTeachplanTree(courseId);
        coursePreviewDto.setTeachplans(teachplanTree);

        // 获取教师信息
        List<CourseTeacher> courseTeachers = courseTeacherInfoService.queryTeachers(courseId);
        coursePreviewDto.setCourseTeacher(courseTeachers);

        return coursePreviewDto;

    }

    @Override
    public void commitAudit(Long companyId, Long courseId) {
        // 检查约束
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        String status = courseBase.getStatus();
        if("202003".equals(status)){
            PEPlusException.cast("课程当前正在审核中，审核完成后可再提交");
        }
        if(!companyId.equals(courseBase.getCompanyId())){
            PEPlusException.cast("本机构只能提交本机构的课程");
        }
        // 课程图片
        if (StringUtils.isEmpty(courseBase.getPic())) {
            PEPlusException.cast("提交失败，需要上传课程图片");
        }
        // 课程计划
        List<TeachplanDto> teachplans = teachplanService.findTeachplanTree(courseId);
        if(teachplans.size() <= 0){
            PEPlusException.cast("提交失败，需要添加课程计划");
        }

        //添加课程预发布记录
        CoursePublishPre coursePublishPre = new CoursePublishPre();
        //课程基本信息加部分营销信息
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseById(courseId);
        BeanUtils.copyProperties(courseBaseInfo,coursePublishPre);
        //课程营销信息
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        //转为json
        String courseMarketJson = JSON.toJSONString(courseMarket);
        //将课程营销信息json数据放入课程预发布表
        coursePublishPre.setMarket(courseMarketJson);
        // 课程计划放入数据
        String teachplansString = JSON.toJSONString(teachplans);
        coursePublishPre.setTeachplan(teachplansString);
        // 教师信息
        List<CourseTeacher> courseTeachers = courseTeacherInfoService.queryTeachers(courseId);
        coursePublishPre.setTeachers(JSON.toJSONString(courseTeachers));

        coursePublishPre.setCreateDate(LocalDateTime.now());
        coursePublishPre.setStatus("202003");

        // 写入数据
        CoursePublishPre coursePublishPre1 = coursePublishPreMapper.selectById(courseId);
        if(coursePublishPre1 != null){
            coursePublishPreMapper.updateById(coursePublishPre);
        }else {
            coursePublishPreMapper.insert(coursePublishPre);
        }

        // 更新基本表数据
        courseBase.setAuditStatus("202003");
        courseBaseMapper.updateById(courseBase);
    }

    @Override
    @Transactional
    public void coursepublish(Long companyId, Long courseId) {
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
        // 校验约束
        if(coursePublishPre == null){
            PEPlusException.cast("请先提交课程审核，审核通过才能发布");
        }

        if(!companyId.equals(coursePublishPre.getCompanyId())){
            PEPlusException.cast("只能发布本机构的课程");
        }
        if(!coursePublishPre.getStatus().equals("202004")){
            PEPlusException.cast("只能发布审核通过的课程");
        }

        // 保存课程发布信息
        saveCoursePublish(courseId);

        // 保存发布信息
        saveCoursePublishMessage(courseId);

        // 删除课程预发布表的对应数据
        coursePublishPreMapper.deleteById(courseId);

    }

    /**
     * @description 保存课程发布信息
     * @param courseId  课程id
     * @return void
     * @author khran
     * @date 2022/9/20 16:32
     */
    private void saveCoursePublish(Long courseId){
        //整合课程发布信息
        //查询课程预发布表
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
        if(coursePublishPre == null){
            PEPlusException.cast("课程预发布数据为空");
        }

        CoursePublish coursePublish = new CoursePublish();

        //拷贝到课程发布对象
        BeanUtils.copyProperties(coursePublishPre,coursePublish);
        coursePublish.setStatus("203002");
        CoursePublish coursePublishUpdate = coursePublishMapper.selectById(courseId);
        if(coursePublishUpdate == null){
            coursePublishMapper.insert(coursePublish);
        }else{
            coursePublishMapper.updateById(coursePublish);
        }
        //更新课程基本表的发布状态
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        courseBase.setStatus("203002");
        courseBaseMapper.updateById(courseBase);
    }

    public void saveCoursePublishMessage(Long courseId){
        MqMessage coursePublish = mqMessageService.addMessage("course_publish", String.valueOf(courseId), null, null);
        if(coursePublish == null){
            PEPlusException.cast(CommonError.UNKOWN_ERROR);
        }
    }
}
