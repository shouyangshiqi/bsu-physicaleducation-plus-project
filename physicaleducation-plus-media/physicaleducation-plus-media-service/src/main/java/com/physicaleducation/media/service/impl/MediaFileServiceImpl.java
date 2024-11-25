package com.physicaleducation.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.physicaleducation.execption.PEPlusException;
import com.physicaleducation.media.mapper.MediaFilesMapper;
import com.physicaleducation.media.model.dto.QueryMediaParamsDto;
import com.physicaleducation.media.model.dto.UploadFileParamsDto;
import com.physicaleducation.media.model.dto.UploadFileResultDto;
import com.physicaleducation.media.model.po.MediaFiles;
import com.physicaleducation.media.service.MediaFileService;
import com.physicaleducation.model.PageParams;
import com.physicaleducation.model.PageResult;
import com.physicaleducation.model.RestResponse;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Mr.M
 * @version 1.0
 * @description TODO
 * @date 2022/9/10 8:58
 */
@Service
@Slf4j
public class MediaFileServiceImpl implements MediaFileService {

    @Autowired
    MediaFilesMapper mediaFilesMapper;

    @Autowired
    MinioClient minioClient;

    @Value("${minio.bucket.files}")
    private String bucket_Files;

    @Value("${minio.bucket.videofiles}")
    private String bucket_vedio;

    @Autowired
    private MediaFileService mediaFileService;

    @Override
    public PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto) {

        //构建查询条件对象
        LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();

        //分页对象
        Page<MediaFiles> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        // 查询数据内容获得结果
        Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
        // 获取数据列表
        List<MediaFiles> list = pageResult.getRecords();
        // 获取数据总数
        long total = pageResult.getTotal();
        // 构建结果集
        PageResult<MediaFiles> mediaListResult = new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());
        return mediaListResult;
    }

    @Override
    public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String localFilePath) {
        // 将文件上传到minio保存
        // 确保文件存在
         File file = new File(localFilePath);
        if(file == null){
            PEPlusException.cast("文件不存在");
        }
        //文件名称
        String filename = uploadFileParamsDto.getFilename();
        //文件扩展名
        String extension = filename.substring(filename.lastIndexOf("."));
        // 获取文件的mimeType
        String mimeType = getMimeType(extension);
        // 获取文件的md5值
        String fileMd5 = getFileMd5(file);
        // 获取文件的默认目录
        String defaultFolderPath = getDefaultFolderPath();
        // 获取存储到minio中的对象名
        String objectName = defaultFolderPath + fileMd5 + extension;
        // 将文件上传到minio中
        boolean flag = addMediaFilesToMinIO(localFilePath, mimeType, bucket_Files, objectName);

        // 将文件信息保存到数据库中
        // 设置文件大小
        uploadFileParamsDto.setFileSize(file.length());
        // 将文件信息存储到数据库中
        MediaFiles mediaFiles = mediaFileService.addMediaFilesToDb(companyId, fileMd5, uploadFileParamsDto, bucket_Files, objectName);
        // 准备返回数据
        UploadFileResultDto uploadFileResultDto = new UploadFileResultDto();
        BeanUtils.copyProperties(mediaFiles, uploadFileResultDto);
        return uploadFileResultDto;
    }

    private String getMimeType(String extension){
        if(extension == null){
            extension = "";
        }
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;//通用mimeType，字节流
        if (extensionMatch != null) {
            mimeType = extensionMatch.getMimeType();
        }
        return mimeType;
    }

    private String getFileMd5(File file) {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            String fileMd5 = DigestUtils.md5Hex(fileInputStream);
            return fileMd5;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getDefaultFolderPath(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String folder = sdf.format(new Date()).replace("-", "/")+"/";
        return folder;
    }


    private boolean addMediaFilesToMinIO(String localFilePath, String mimeType, String bucket, String objectName){
        try {
            UploadObjectArgs testbucket = UploadObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .filename(localFilePath)
                    .contentType(mimeType)
                    .build();
            minioClient.uploadObject(testbucket);
            log.debug("上传文件到minio成功,bucket:{},objectName:{}",bucket,objectName);
            System.out.println("上传成功");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("上传文件到minio出错,bucket:{},objectName:{},错误原因:{}",bucket,objectName,e.getMessage(),e);
        }
        return false;
    }

    @Transactional
    public MediaFiles addMediaFilesToDb(Long companyId, String fileMd5, UploadFileParamsDto uploadFileParamsDto, String bucket, String objectName){
        // 数据库查询该数据
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if(mediaFiles == null){
            mediaFiles = new MediaFiles();
            //拷贝基本信息
            BeanUtils.copyProperties(uploadFileParamsDto, mediaFiles);
            mediaFiles.setId(fileMd5);
            mediaFiles.setFileId(fileMd5);
            mediaFiles.setCompanyId(companyId);
            mediaFiles.setUrl("/" + bucket + "/" + objectName);
            mediaFiles.setBucket(bucket);
            mediaFiles.setFilePath(objectName);
            mediaFiles.setCreateDate(LocalDateTime.now());
            mediaFiles.setAuditStatus("002003");
            mediaFiles.setStatus("1");

            // 保存文件到数据表
            int insert = mediaFilesMapper.insert(mediaFiles);
            if(insert <= 0){
                log.error("保存文件信息到数据库失败,{}",mediaFiles.toString());
                PEPlusException.cast("上传后保存文件到数据库失败");
            }
            log.debug("保存文件信息到数据库成功,{}",mediaFiles.toString());
        }
        return mediaFiles;
    }

    @Override
    public RestResponse checkFile(String fileMd5) {
        // 先查询数据库中是否存在
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if(mediaFiles != null){
            // 桶名
            String bucket = mediaFiles.getBucket();
            // 路径名
            String filePath = mediaFiles.getFilePath();
            // 获取minio中的数据
            try {
                GetObjectArgs data = GetObjectArgs.builder()
                        .bucket(bucket)
                        .object(filePath)
                        .build();
                GetObjectResponse object = minioClient.getObject(data);
                if(object != null){
                    return RestResponse.success(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return RestResponse.success(false);
    }

    @Override
    public RestResponse checkChunk(String fileMd5, int chunkIndex) {
        // 分页文件目录
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        String chunkFilePath =  chunkFileFolderPath + chunkIndex;

        // 获取数据

        try {
            GetObjectResponse object = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucket_vedio)
                    .object(chunkFilePath)
                    .build()
            );
            if(object != null){
                return RestResponse.success(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //分块未存在
        return RestResponse.success(false);
    }

    @Override
    public RestResponse uploadChunk(String fileMd5, String localChunkFilePath, int chunk) {
        // 分页文件目录
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        String chunkFilePath =  chunkFileFolderPath + chunk;

        String mimeType = getMimeType(null);
        boolean flag = addMediaFilesToMinIO(localChunkFilePath, mimeType, bucket_vedio, chunkFilePath);
        if(!flag){
            log.debug("上传分块文件失败:{}", chunkFilePath);
            return RestResponse.success(false);
        }
        log.debug("上传分块文件成功:{}", chunkFilePath);
        return RestResponse.success(true);
    }

    @Override
    public RestResponse mergechunks(Long companyId, String fileMd5, int chunkTotal, UploadFileParamsDto uploadFileParamsDto) {
        // 获取分块文件路径
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);

        // 将分块文件组成 List<ComposeSourse>
        List<ComposeSource> sourceObjectList = Stream.iterate(0, i -> ++i)
                .limit(chunkTotal)
                .map(i -> ComposeSource.builder()
                        .bucket(bucket_vedio)
                        .object(chunkFileFolderPath + i)
                        .build())
                .collect(Collectors.toList());

        // 得到数据后将数据进行合并
        //文件名称
        String fileName = uploadFileParamsDto.getFilename();
        //文件扩展名
        String extName = fileName.substring(fileName.lastIndexOf("."));
        //合并文件路径
        String mergeFilePath = getFilePathByMd5(fileMd5, extName);



        try {
            // ==========合并数据=============
            ComposeObjectArgs composeObjectArgs = ComposeObjectArgs.builder()
                    .bucket(bucket_vedio)
                    .object(mergeFilePath)
                    .sources(sourceObjectList)
                    .build();
            minioClient.composeObject(composeObjectArgs);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("合并文件失败,fileMd5:{},异常:{}",fileMd5,e.getMessage(),e);
            return RestResponse.success(false);
        }

        // 校验合并后的文件是否和源文件一致
        File file = downloadFileFromMinIO(bucket_vedio, mergeFilePath);

        try(FileInputStream fileInputStream = new FileInputStream(file)) {
            String mergeFile_md5 = DigestUtils.md5Hex(fileInputStream);
            // 比较两个md5
            if(!fileMd5.equals(mergeFile_md5)){
                return RestResponse.validfail(false, "文件合并校验失败，最终上传失败。");
            }
            uploadFileParamsDto.setFileSize(file.length());
        } catch (Exception e) {
            log.debug("校验文件失败,fileMd5:{},异常:{}",fileMd5,e.getMessage(),e);
            return RestResponse.validfail(false, "文件合并校验失败，最终上传失败。");
        }finally {
            if(file!=null){
                file.delete();
            }
        }

        // ================文件信息入库================
        MediaFiles mediaFiles =  mediaFileService.addMediaFilesToDb(companyId, fileMd5, uploadFileParamsDto, bucket_vedio, mergeFilePath);
        if(mediaFiles == null){
            return  RestResponse.success(false, "文件入库失败");
        }

        // 清理分块文件
        clearChunkFiles(chunkFileFolderPath,chunkTotal);
        return RestResponse.success(true);
    }

    /**
     * 查询出该分块的文件目录
     * @param fileMd5
     * @return
     */
    private String getChunkFileFolderPath(String fileMd5){
        String folederPath = fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + "chunk" + "/";
        return folederPath;
    }

    private String getFilePathByMd5(String fileMd5,String fileExt){
        return   fileMd5.substring(0,1) + "/" + fileMd5.substring(1,2) + "/" + fileMd5 + "/" +fileMd5 +fileExt;
    }

    /**
     * 下载minio文件
     * @param bucket
     * @param objectName
     * @return
     */
    private File downloadFileFromMinIO(String bucket, String objectName){
        //临时文件
        File minioFile = null;
        FileOutputStream outputStream = null;

        try {
            GetObjectResponse object = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .build()
            );
            minioFile=File.createTempFile("minio", ".merge");
            outputStream = new FileOutputStream(minioFile);
            IOUtils.copy(object,outputStream);
            // 下载文件的步骤,先利用minio的minioClient.getObject得到文件输入流，再定义一个新文件和FileOutputStream流，将output流绑定到文件上，将输入流复制到输出流中，返回文件最终的结果
            return minioFile;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(outputStream!=null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 清除分块文件
     * @param chunkFileFolderPath 分块文件路径
     * @param chunkTotal 分块文件总数
     */
    private void clearChunkFiles(String chunkFileFolderPath,int chunkTotal) {

        try {
            List<DeleteObject> deleteObjects = Stream.iterate(0, i -> ++i)
                    .limit(chunkTotal)
                    .map(i -> new DeleteObject(chunkFileFolderPath + i))
                    .collect(Collectors.toList());

            RemoveObjectsArgs removeObjectsArgs = RemoveObjectsArgs.builder().bucket(bucket_vedio).objects(deleteObjects).build();
            Iterable<Result<DeleteError>> results = minioClient.removeObjects(removeObjectsArgs);
            results.forEach(r -> {
                DeleteError deleteError = null;
                try {
                    deleteError = r.get();
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("清楚分块文件失败,objectname:{}", deleteError.objectName(), e);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            log.error("清楚分块文件失败,chunkFileFolderPath:{}", chunkFileFolderPath, e);
        }
    }
}
