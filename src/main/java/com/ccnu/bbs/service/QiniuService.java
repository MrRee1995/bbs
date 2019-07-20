package com.ccnu.bbs.service;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;

import java.io.File;
import java.io.InputStream;

public interface QiniuService {

    /**
     * 上传文件
     * <p>
     * (文件上传)
     *
     * @param file
     * @return
     * @throws QiniuException
     */
    String uploadFile(File file, String key) throws QiniuException;

    /**
     * 上传文件
     * <p>
     * (文件流上传)
     *
     * @param inputStream
     * @return
     * @throws QiniuException
     */
    String uploadFile(InputStream inputStream, String key) throws QiniuException;

    /**
     * 删除
     *
     * @param key
     * @return
     * @throws QiniuException
     */
    Response delete(String key) throws QiniuException;
}
