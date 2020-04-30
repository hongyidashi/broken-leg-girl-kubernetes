package com.blg.security.config;

import com.blg.security.filter.HttpBasicAuthorizeFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 描述：配置类
 * @Auther: panhongtong
 * @Date: 2020/4/30 14:51
 */
@Configuration
public class AuthConfig {

    @Bean
    public HttpBasicAuthorizeFilter httpBasicAuthorizeFilter() {
        return new HttpBasicAuthorizeFilter();
    }
}
