package com.ccnu.bbs.service.Impl;

import com.ccnu.bbs.config.QiNiuAccountConfig;
import com.ccnu.bbs.service.QiniuService;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;

@Service
public class QiniuServiceImpl implements QiniuService {

    @Autowired
    private UploadManager uploadManager;

    @Autowired
    private BucketManager bucketManager;

    @Autowired
    private Gson gson;

    @Autowired
    private Auth auth;

    @Autowired
    private QiNiuAccountConfig qiNiuAccountConfig;


    @Override
    /**
     * 以文件方式上传
     */
    public String uploadFile(File file, String key) throws QiniuException {

        // 上传，若失败重传3次
        Response response = this.uploadManager.put(file, key, getUploadToken());
        int retry = 0;
        while (response.needRetry() && retry < 3) {
            response = this.uploadManager.put(file, null, getUploadToken());
            retry++;
        }
        // 解析上传成功的结果
        DefaultPutRet putRet = gson.fromJson(response.bodyString(), DefaultPutRet.class);
        return qiNiuAccountConfig.getPath() + "/" + putRet.key;
    }

    @Override
    /**
     * 以文件流方式上传
     */
    public String uploadFile(InputStream inputStream, String key) throws QiniuException {

        // 上传，若失败重传3次
        Response response = this.uploadManager.put(inputStream, key, getUploadToken(), null, null);
        int retry = 0;
        while (response.needRetry() && retry < 3) {
            response = this.uploadManager.put(inputStream, null, getUploadToken(), null, null);
            retry++;
        }
        // 解析上传成功的结果
        DefaultPutRet putRet = gson.fromJson(response.bodyString(), DefaultPutRet.class);
        return qiNiuAccountConfig.getPath() + "/" + putRet.key;
    }

    @Override
    /**
     * 删除上传的文件
     */
    public Response delete(String key) throws QiniuException {
        Response response = bucketManager.delete(qiNiuAccountConfig.getBucket(), key);
        int retry = 0;
        while (response.needRetry() && retry++ < 3) {
            response = bucketManager.delete(qiNiuAccountConfig.getBucket(), key);
        }
        return response;
    }

    /**
     * 获取上传凭证
     * @return
     */
    private String getUploadToken() {
        return this.auth.uploadToken(qiNiuAccountConfig.getBucket());
    }

}
