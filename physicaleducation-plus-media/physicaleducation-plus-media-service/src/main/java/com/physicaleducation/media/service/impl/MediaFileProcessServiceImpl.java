package com.physicaleducation.media.service.impl;

import com.physicaleducation.media.mapper.MediaProcessHistoryMapper;
import com.physicaleducation.media.mapper.MediaProcessMapper;
import com.physicaleducation.media.model.po.MediaProcess;
import com.physicaleducation.media.model.po.MediaProcessHistory;
import com.physicaleducation.media.service.MediaFileProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author khran
 * @version 1.0
 * @description
 */

@Service
@Slf4j
public class MediaFileProcessServiceImpl implements MediaFileProcessService {

    @Autowired
    private MediaProcessMapper mediaProcessMapper;

    @Autowired
    private MediaProcessHistoryMapper mediaProcessHistoryMapper;
    @Override
    public List<MediaProcess> getMediaProcessList(int shardIndex, int shardTotal, int count) {
        List<MediaProcess> mediaProcesses = mediaProcessMapper.selectListByShardIndex(shardTotal, shardIndex, count);
        return mediaProcesses;
    }

    @Override
    public boolean startTask(long id) {
        int i = mediaProcessMapper.startTask(id);
        return i > 0 ? true:false;
    }

    @Override
    public void saveProcessFinishStatus(Long taskId, String status, String fileId, String url, String errorMsg) {
        // 更新任务状态
        // 获取任务
        MediaProcess mediaProcess = mediaProcessMapper.selectById(taskId);
        if(mediaProcess == null){
            return;
        }

        // 处理失败
        if(status.equals("3")){
            mediaProcess.setStatus(status);
            mediaProcess.setErrormsg(errorMsg);
            mediaProcess.setFailCount(mediaProcess.getFailCount() + 1);
            mediaProcessMapper.updateById(mediaProcess);
            log.debug("更新任务处理状态为失败，任务信息:{}",mediaProcess);
        }

        // 任务处理成功
        mediaProcess.setUrl(url);
        mediaProcess.setStatus("2");
        mediaProcess.setFinishDate(LocalDateTime.now());


        // 加入到历史记录中
        MediaProcessHistory mediaProcessHistory = new MediaProcessHistory();
        BeanUtils.copyProperties(mediaProcess, mediaProcessHistory);
        int insert = mediaProcessHistoryMapper.insert(mediaProcessHistory);
        if(insert <= 0){
            log.info("数据{}插0入到历史记录失败", mediaProcessHistory);
        }
        mediaProcessMapper.deleteById(mediaProcess);
    }
}
