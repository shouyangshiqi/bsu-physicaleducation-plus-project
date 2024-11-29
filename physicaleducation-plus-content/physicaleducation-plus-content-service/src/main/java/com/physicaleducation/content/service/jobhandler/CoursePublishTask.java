package com.physicaleducation.content.service.jobhandler;

import com.physicaleducation.content.feignclient.SearchServiceClient;
import com.physicaleducation.content.mapper.CoursePublishMapper;
import com.physicaleducation.content.model.po.CourseIndex;
import com.physicaleducation.content.model.po.CoursePublish;
import com.physicaleducation.content.service.CoursePublishService;
import com.physicaleducation.execption.PEPlusException;
import com.physicaleducation.messagesdk.model.po.MqMessage;
import com.physicaleducation.messagesdk.service.MessageProcessAbstract;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * @author khran
 * @version 1.0
 * @description
 */
@Component
@Slf4j
public class CoursePublishTask extends MessageProcessAbstract {

    @Autowired
    private CoursePublishService coursePublishService;

    @Autowired
    private CoursePublishMapper coursePublishMapper;

    @Autowired
    private SearchServiceClient searchServiceClient;


    //任务调度入口
    @XxlJob("CoursePublishJobHandler")
    public void coursePublishJobHandler() throws Exception {
        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        log.debug("shardIndex="+shardIndex+",shardTotal="+shardTotal);
        //参数:分片序号、分片总数、消息类型、一次最多取到的任务数量、一次任务调度执行的超时时间
        process(shardIndex,shardTotal,"course_publish",30,60);
    }

    //课程发布任务处理
    @Override
    public boolean execute(MqMessage mqMessage) {
        //获取消息相关的业务信息
        String businessKey1 = mqMessage.getBusinessKey1();
        long courseId = Integer.parseInt(businessKey1);
        //课程静态化
        generateCourseHtml(mqMessage,courseId);
        //课程索引
        saveCourseIndex(mqMessage,courseId);
        //课程缓存
        saveCourseCache(mqMessage,courseId);
        return true;
    }

    //生成课程静态化页面并上传至文件系统
    public void generateCourseHtml(MqMessage mqMessage,long courseId){
        //静态化文件
        log.debug("开始进行课程静态化,课程id:{}",courseId);
        //消息id
        Long id = mqMessage.getId();

        //消息幂等性处理
        int stageOne = mqMessageService.getStageOne(id);
        if(stageOne >0){
            log.debug("课程静态化已处理直接返回，课程id:{}",courseId);
            return ;
        }

        // 处理页面静态化
        File htmlFile = coursePublishService.generateCourseHtml(courseId);
        if(htmlFile != null) {
            coursePublishService.uploadCourseHtml(courseId, htmlFile);
        }
        //保存第一阶段状态
        mqMessageService.completedStageOne(id);
    }


    //将课程信息缓存至redis
    public void saveCourseCache(MqMessage mqMessage,long courseId){
        log.debug("将课程信息缓存至redis,课程id:{}",courseId);
        // TODO: 将课程信息保存到Redis中
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    //保存课程索引信息
    public void saveCourseIndex(MqMessage mqMessage,long courseId){
        log.debug("保存课程索引信息,课程id:{}",courseId);

        // 检查索引是否已经添加
        Long id = mqMessage.getId();
        int stageTwo = mqMessageService.getStageTwo(id);
        if(stageTwo > 0){
            log.debug("课程索引已处理直接返回，课程id:{}",courseId);
            return ;
        }

        // 取出课程发布的信息
        CoursePublish coursePublish = coursePublishMapper.selectById(courseId);
        CourseIndex courseIndex = new CourseIndex();
        BeanUtils.copyProperties(coursePublish, courseIndex);

        Boolean add = searchServiceClient.add(courseIndex);
        if(!add){
            PEPlusException.cast("添加索引失败");
        }

        // 保存状态
        mqMessageService.completedStageTwo(id);

    }
}
