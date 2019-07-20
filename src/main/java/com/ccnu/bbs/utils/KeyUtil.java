package com.ccnu.bbs.utils;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.UUID;

public class KeyUtil {

    /**
     * 生成唯一的主键UUID
     * @return
     */
    public static synchronized String genUniqueKey(){

        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }

    /**
     * MD5加密的sessionId
     * @param key
     * @return
     */
    public static synchronized String getSessionId(String key) {
        String sessionId = DigestUtils.md5Hex(key);
        System.out.println("MD5加密后的sessionId为：" + sessionId);
        return sessionId;
    }
}
