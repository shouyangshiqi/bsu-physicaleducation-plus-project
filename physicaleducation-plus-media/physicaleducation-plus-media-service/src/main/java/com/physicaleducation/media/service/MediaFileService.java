package com.physicaleducation.media.service;


import com.physicaleducation.media.model.dto.QueryMediaParamsDto;
import com.physicaleducation.media.model.dto.UploadFileParamsDto;
import com.physicaleducation.media.model.dto.UploadFileResultDto;
import com.physicaleducation.media.model.po.MediaFiles;
import com.physicaleducation.model.PageParams;
import com.physicaleducation.model.PageResult;

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
}
