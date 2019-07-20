package com.ccnu.bbs.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "wechat")
public class WeChatAccountCogfig {

    /**
     * 公众平台id
     */
    private String appId;

    /**
     * 公众平台secret
     */
    private String appSecret;
}
