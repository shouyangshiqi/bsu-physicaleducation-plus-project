package com.physicaleducation.content;

import com.physicaleducation.content.config.MultipartSupportConfig;
import com.physicaleducation.content.feignclient.MediaServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @author khran
 * @version 1.0
 * @description
 */

@SpringBootTest
public class FeignUploadTest {

    @Autowired
    MediaServiceClient mediaServiceClient;

    //远程调用，上传文件
    @Test
    public void test() {

        MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(new File("D:\\java_learn\\idea-program\\html-file\\test.html"));
        mediaServiceClient.uploadFile(multipartFile,"course/test.html");
    }

}