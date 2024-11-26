package com.physicaleducation.media.service.jobhandler;

import com.physicaleducation.media.model.po.MediaProcess;
import com.physicaleducation.media.service.MediaFileProcessService;
import com.physicaleducation.media.service.MediaFileService;
import com.physicaleducation.media.service.impl.MediaFileServiceImpl;
import com.physicaleducation.utils.Mp4VideoUtil;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author khran
 * @version 1.0
 * @description
 */

@Component
@Slf4j
public class VideoTask {

    @Autowired
    private MediaFileProcessService mediaFileProcessService;

    @Autowired
    private MediaFileServiceImpl mediaFileService;

    @Value("${videoprocess.ffmpegpath}")
    String ffmpegpath;

    /**
     * 2、分片广播任务
     */
    @XxlJob("VideoJobHandler")
    public void shardingJobHandler() throws Exception {

        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();

        // 确定cpu的核心数
        int processors = Runtime.getRuntime().availableProcessors();

        // 查询需要处理的任务
        List<MediaProcess> mediaProcessList = mediaFileProcessService.getMediaProcessList(shardIndex, shardTotal, processors);
        int size = mediaProcessList.size();

        // 创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(size);

        //计数器
        CountDownLatch countDownLatch = new CountDownLatch(size);
        // 开启任务
        for (MediaProcess mediaProcess : mediaProcessList) {
            // 将任务加入线程池
            executorService.execute(() -> {
                try {
                    // 开启任务
                    Long taskId = mediaProcess.getId();
                    boolean flag = mediaFileProcessService.startTask(taskId);
                    if (!flag) {
                        return;
                    }
                    log.debug("开始执行任务:{}", mediaProcess);
                    // 执行视频转码
                    //桶
                    String bucket = mediaProcess.getBucket();
                    //存储路径
                    String filePath = mediaProcess.getFilePath();
                    //原始视频的md5值
                    String fileId = mediaProcess.getFileId();
                    //原始文件名称
                    String filename = mediaProcess.getFilename();
                    // 下载文件
                    File originalFile = mediaFileService.downloadFileFromMinIO(bucket, filePath);
                    if (originalFile == null) {
                        log.debug("下载待处理文件失败,originalFile:{}", mediaProcess.getBucket().concat(mediaProcess.getFilePath()));
                        mediaFileProcessService.saveProcessFinishStatus(mediaProcess.getId(), "3", fileId, null, "下载待处理文件失败");
                        return;
                    }


                    // 处理下载文件
                    File mp4File = null;
                    try {
                        mp4File = File.createTempFile("mp4", ".mp4");
                    } catch (IOException e) {
                        log.error("创建mp4临时文件失败");
                        mediaFileProcessService.saveProcessFinishStatus(mediaProcess.getId(), "3", fileId, null, "创建mp4临时文件失败");
                        return;
                    }
                    //视频处理结果
                    String result = "";
                    try {
                        //开始处理视频
                        Mp4VideoUtil videoUtil = new Mp4VideoUtil(ffmpegpath, originalFile.getAbsolutePath(), mp4File.getName(), mp4File.getAbsolutePath());
                        //开始视频转换，成功将返回success
                        result = videoUtil.generateMp4();
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error("处理视频文件:{},出错:{}", mediaProcess.getFilePath(), e.getMessage());
                    }
                    if (!result.equals("success")) {
                        //记录错误信息
                        log.error("处理视频失败,视频地址:{},错误信息:{}", bucket + filePath, result);
                        mediaFileProcessService.saveProcessFinishStatus(mediaProcess.getId(), "3", fileId, null, result);
                        return;
                    }

                    // 上传到minio
                    String objectName = getFilePath(fileId, ".mp4");
                    String url =  "/" + bucket + "/" + objectName;
                    try {
                        boolean flag1 = mediaFileService.addMediaFilesToMinIO(mp4File.getAbsolutePath(), "video/mp4", bucket, objectName);
                        if(!flag1){
                            log.error("上传视频失败失败,视频地址:{}", bucket + objectName);
                        }
                        mediaFileProcessService.saveProcessFinishStatus(mediaProcess.getId(), "2", fileId, url, null);
                    } catch (Exception e) {
                        log.error("上传视频失败或入库失败,视频地址:{},错误信息:{}", bucket + objectName, e.getMessage());
                        mediaFileProcessService.saveProcessFinishStatus(mediaProcess.getId(), "3", fileId, null, "处理后视频上传或入库失败");
                    }
                } finally {
                    countDownLatch.countDown();
                }
            });
            countDownLatch.await(30, TimeUnit.MINUTES);
        }
    }
    private String getFilePath(String fileMd5,String fileExt){
        return   fileMd5.substring(0,1) + "/" + fileMd5.substring(1,2) + "/" + fileMd5 + "/" +fileMd5 +fileExt;
    }
}
