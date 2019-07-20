package com.ccnu.bbs.config;

import cn.binarywang.wx.miniapp.api.WxMaSecCheckService;
import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaSecCheckServiceImpl;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.WxMaInMemoryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WeChatMaConfig {

    @Autowired
    WeChatAccountCogfig weChatAccountCogfig;

    @Bean
    public WxMaSecCheckService wxMaSecCheckService(){
        WxMaSecCheckService wxMaSecCheckService = new WxMaSecCheckServiceImpl(wxMaService());
        return wxMaSecCheckService;
    }

    @Bean
    public WxMaService wxMaService(){
        WxMaService wxMaService = new WxMaServiceImpl();
        wxMaService.setWxMaConfig(wxMaConfigStorage());
        return wxMaService;
    }

    @Bean
    public WxMaInMemoryConfig wxMaConfigStorage(){
        WxMaInMemoryConfig wxMaInMemoryConfig = new WxMaInMemoryConfig();
        wxMaInMemoryConfig.setAppid(weChatAccountCogfig.getAppId());
        wxMaInMemoryConfig.setSecret(weChatAccountCogfig.getAppSecret());
        return wxMaInMemoryConfig;
    }
}
