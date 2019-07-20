package com.ccnu.bbs.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "qiniu")
public class QiNiuAccountConfig {

    private String accessKey;

    private String secretKey;
    /** 创建的存储空间名. */
    private String bucket;
    /** 存储空间的访问域名. */
    private String path;
}
