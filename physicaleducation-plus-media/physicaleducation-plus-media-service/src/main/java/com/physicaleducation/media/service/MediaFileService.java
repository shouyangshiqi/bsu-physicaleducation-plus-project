package com.physicaleducation.media.service;


import com.physicaleducation.media.model.dto.QueryMediaParamsDto;
import com.physicaleducation.media.model.dto.UploadFileParamsDto;
import com.physicaleducation.media.model.dto.UploadFileResultDto;
import com.physicaleducation.media.model.po.MediaFiles;
import com.physicaleducation.model.PageParams;
import com.physicaleducation.model.PageResult;
import com.physicaleducation.model.RestResponse;

/**
 * @author Mr.M
 * @version 1.0
 * @description 媒资文件管理业务类
 * @date 2022/9/10 8:55
 */
public interface MediaFileService {

    /**
     * @param pageParams          分页参数
     * @param queryMediaParamsDto 查询条件
     * @return com.xuecheng.base.model.PageResult<com.xuecheng.media.model.po.MediaFiles>
     * @description 媒资文件查询方法
     * @author Mr.M
     * @date 2022/9/10 8:57
     */
    public PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);

    /**
     * 上传文件
     * @param companyId 上传文件公司id
     * @param uploadFileParamsDto   上传文件信息
     * @param localFilePath     文件磁盘路径
     * @return      文件信息
     */
    public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String localFilePath);

    /**
     * 将文件信息上传到数据库中，为了减小事务的范围，利用注入自己类生成代理对象，代理对象执行事务方法，因此需要在interface中规定该方法
     * @param companyId
     * @param fileMd5
     * @param uploadFileParamsDto
     * @param bucketFiles
     * @param objectName
     * @return
     */
    MediaFiles addMediaFilesToDb(Long companyId, String fileMd5, UploadFileParamsDto uploadFileParamsDto, String bucketFiles, String objectName);

    /**
     * 检查文件是否存在
     * @param fileMd5
     * @return
     */
    RestResponse checkFile(String fileMd5);

    /**
     * 检查分块是否存在
     * @param fileMd5
     * @param chunkIndex
     * @return
     */
    RestResponse checkChunk(String fileMd5, int chunkIndex);

    /**
     * 上传数据
     * @param fileMd5
     * @param localChunkFilePath
     * @param chunkTotal
     * @return
     */
    RestResponse uploadChunk(String fileMd5, String localChunkFilePath, int chunkTotal);

    /**
     * 把分块信息全部合并
     * @param companyId
     * @param fileMd5
     * @param chunkTotal
     * @param uploadFileParamsDto
     * @return
     */
    RestResponse mergechunks(Long companyId, String fileMd5, int chunkTotal, UploadFileParamsDto uploadFileParamsDto);

    /**
     * 将待处理视频数据加入到相应数据库中
     * @param mediaFiles
     */
    void addWaitingTask(MediaFiles mediaFiles);

    /**
     * 根据媒资id获取媒资信息
     * @param mediaId
     * @return
     */
    MediaFiles getFileById(String mediaId);
}
