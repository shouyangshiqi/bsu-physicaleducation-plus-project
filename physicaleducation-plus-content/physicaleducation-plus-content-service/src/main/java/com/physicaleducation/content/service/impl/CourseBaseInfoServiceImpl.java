package com.physicaleducation.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.physicaleducation.content.mapper.*;
import com.physicaleducation.content.model.dto.AddCourseDto;
import com.physicaleducation.content.model.dto.CourseBaseInfoDto;
import com.physicaleducation.content.model.dto.EditCourseDto;
import com.physicaleducation.content.model.dto.QueryCourseParamsDto;
import com.physicaleducation.content.model.po.*;
import com.physicaleducation.content.service.CourseBaseInfoService;
import com.physicaleducation.execption.PEPlusException;
import com.physicaleducation.model.PageParams;
import com.physicaleducation.model.PageResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author khran
 * @version 1.0
 * @description
 */

@Service
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {

    @Resource
    private CourseBaseMapper courseBaseMapper;
    @Resource
    private TeachplanMapper teachplanMapper;
    @Resource
    private CourseTeacherMapper courseTeacherMapper;

    @Autowired
    private CourseMarketMapper courseMarketMapper;

    @Autowired
    private CourseCategoryMapper courseCategoryMapper;

    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto) {

        // 拼装查询条件
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        // 根据名称去模糊查询 name like "%名称%"
        queryWrapper.like(StringUtils.isNotEmpty(queryCourseParamsDto.getCourseName()),CourseBase::getName,queryCourseParamsDto.getCourseName());

        // 根据课程审核状态去查询 course_base.audit_status = ?
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getAuditStatus()), CourseBase::getAuditStatus, queryCourseParamsDto.getAuditStatus());

        // 根据课程发布状态去查询
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getPublishStatus()),CourseBase::getStatus, queryCourseParamsDto.getPublishStatus());


        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        Page<CourseBase> pageResult = courseBaseMapper.selectPage(page, queryWrapper);

        List<CourseBase> items = pageResult.getRecords();

        long total = pageResult.getTotal();

        PageResult<CourseBase> courseBasePageResult = new PageResult<>(items, total, pageParams.getPageNo(), pageParams.getPageSize());

        return courseBasePageResult;
    }

    @Transactional
    @Override
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto dto) {
/*        //合法性校验
        if (StringUtils.isBlank(dto.getName())) {
//            throw new RuntimeException("课程名称为空");
            PEPlusException.cast("课程名称为空");
        }

        if (StringUtils.isBlank(dto.getMt())) {
            throw new RuntimeException("课程分类为空");
        }

        if (StringUtils.isBlank(dto.getSt())) {
            throw new RuntimeException("课程分类为空");
        }

        if (StringUtils.isBlank(dto.getGrade())) {
            throw new RuntimeException("课程等级为空");
        }

        if (StringUtils.isBlank(dto.getTeachmode())) {
            throw new RuntimeException("教育模式为空");
        }

        if (StringUtils.isBlank(dto.getUsers())) {
            throw new RuntimeException("适应人群为空");
        }

        if (StringUtils.isBlank(dto.getCharge())) {
            throw new RuntimeException("收费规则为空");
        }*/

        // 新建基础对象
        CourseBase courseBaseNew = new CourseBase();
        BeanUtils.copyProperties(dto, courseBaseNew);

        //设置审核状态
        courseBaseNew.setAuditStatus("202002");
        //设置发布状态
        courseBaseNew.setStatus("203001");
        //机构id
        courseBaseNew.setCompanyId(companyId);
        //添加时间
        courseBaseNew.setCreateDate(LocalDateTime.now());
        //插入课程基本信息表
        int insert = courseBaseMapper.insert(courseBaseNew);
        if(insert<=0){
            throw new RuntimeException("新增课程基本信息失败");
        }

        // 向课程表中保存课程营销信息
        // 课程营销信息
        CourseMarket courseMarketNew = new CourseMarket();
        BeanUtils.copyProperties(dto, courseMarketNew);
        courseMarketNew.setId(courseBaseNew.getId());

        // 保存课程营销信息
        int i = saveCourseMarket(courseMarketNew);
        if(i<=0){
            throw new RuntimeException("保存课程营销信息失败");
        }

        // 返回课程信息以及营销信息根据课程id
        return  getCourseBaseInfo(courseBaseNew.getId());
    }

    @Override
    public CourseBaseInfoDto getCourseBaseById(Long coursId) {
        CourseBaseInfoDto courseBaseInfo = this.getCourseBaseInfo(coursId);
        return courseBaseInfo;
    }

    @Transactional
    @Override
    public CourseBaseInfoDto updateCourseBase(EditCourseDto dto, Long companyId) {
        // 数据合法性校验
            // 根据具体的业务逻辑去校验
        Long courseId = dto.getId();
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if(courseBase==null){
            PEPlusException.cast("课程不存在");
        }

        //校验本机构只能修改本机构的课程
        if(!courseBase.getCompanyId().equals(companyId)){
            PEPlusException.cast("本机构只能修改本机构的课程");
        }
        // 封装基本数据
        BeanUtils.copyProperties(dto, courseBase);
        courseBase.setChangeDate(LocalDateTime.now());
        // 更新数据库
        int i = courseBaseMapper.updateById(courseBase);

        // 封装营销数据
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        BeanUtils.copyProperties(dto,courseMarket);
        saveCourseMarket(courseMarket);

        // 查询课程信息
        CourseBaseInfoDto courseBaseInfo = this.getCourseBaseInfo(courseId);
        return courseBaseInfo;

    }

    @Override
    public void deleteCourseBase(Long courseId, Long companyId) {
        // 检验参数合规性
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if(courseBase==null){
            PEPlusException.cast("课程不存在");
        }
        if(!courseBase.getCompanyId().equals(companyId)){
            PEPlusException.cast("只能删除本机构的课程");
        }

        String auditStatus = courseBase.getAuditStatus();
        if(!auditStatus.equals("202002") ){
            PEPlusException.cast("只能删除审核状态为未提交的课程");
        }
        // 删除课程基本数据
        courseBaseMapper.deleteById(courseBase);
        // 删除营销信息
        courseMarketMapper.deleteById(courseBase);

        // 删除课程计划
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId, courseId);
        teachplanMapper.delete(queryWrapper);

        // 删除教师信息
        LambdaQueryWrapper<CourseTeacher> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.eq(CourseTeacher::getCourseId, courseId);
        courseTeacherMapper.delete(queryWrapper2);
    }

    // 保存课程营销信息
    private int saveCourseMarket(CourseMarket courseMarketNew){
        // 合规性校验
        //收费规则
        String charge = courseMarketNew.getCharge();
        if(StringUtils.isBlank(charge)){
            throw new RuntimeException("收费规则没有选择");
        }
        //收费规则为收费
        if(charge.equals("201001")){
            if(courseMarketNew.getPrice() == null || courseMarketNew.getPrice().floatValue()<=0){
                throw new RuntimeException("课程为收费价格不能为空且必须大于0");
            }
        }
        // 根据id从营销表中查询以判断是新增还是修改
        CourseMarket courseMarket = courseMarketMapper.selectById(courseMarketNew.getId());
        if(courseMarket == null){
            return courseMarketMapper.insert(courseMarketNew);
        }else {
            BeanUtils.copyProperties(courseMarketNew, courseMarket);
            courseMarket.setId(courseMarketNew.getId());
            return courseMarketMapper.updateById(courseMarket);
        }
    }

    // 返回课程信息和营销信息
    private CourseBaseInfoDto getCourseBaseInfo(Long courseId){
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if(courseBase == null){
            return null;
        }
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase, courseBaseInfoDto);
        if(courseMarket != null) {
            BeanUtils.copyProperties(courseMarket, courseBaseInfoDto);
        }
        //查询分类名称
        CourseCategory courseCategoryBySt = courseCategoryMapper.selectById(courseBase.getSt());
        courseBaseInfoDto.setStName(courseCategoryBySt.getName());
        CourseCategory courseCategoryByMt = courseCategoryMapper.selectById(courseBase.getMt());
        courseBaseInfoDto.setMtName(courseCategoryByMt.getName());

        return courseBaseInfoDto;
    }



}
