package com.blg.security.vo;

import lombok.Data;

/**
 * 用户认证参数类
 * @author: panhongtong
 * @create: 2020-04-29 23:11
 **/
@Data
public class AuthVO {

    private String accessKey;

    private String secretKey;
}
