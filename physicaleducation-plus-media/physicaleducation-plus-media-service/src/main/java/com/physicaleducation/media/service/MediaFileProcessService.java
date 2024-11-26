package com.physicaleducation.media.service;

import com.physicaleducation.media.model.po.MediaProcess;

import java.util.List;

/**
 * @author khran
 * @version 1.0
 * @description
 */

public interface MediaFileProcessService {
    /**
     * 根据分片参数获取任务
     * @param shardIndex 分片序号
     * @param shardTotal 分片总数
     * @param count     获取记录数据
     * @return
     */
    public List<MediaProcess> getMediaProcessList(int shardIndex, int shardTotal, int count);

    /**
     * 开启任务
     * @param id
     * @return
     */
    public boolean startTask(long id);

    /**
     * @description 保存任务结果
     * @param taskId  任务id
     * @param status 任务状态
     * @param fileId  文件id
     * @param url url
     * @param errorMsg 错误信息
     * @return void
     * @author Mr.M
     * @date 2022/10/15 11:29
     */
    void saveProcessFinishStatus(Long taskId,String status,String fileId,String url,String errorMsg);
}
