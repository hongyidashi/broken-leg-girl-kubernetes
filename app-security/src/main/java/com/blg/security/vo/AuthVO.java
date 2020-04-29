package com.blg.security.vo;

import lombok.Data;

/**
 * @author: panhongtong
 * @create: 2020-04-29 23:11
 * @description: 用户认证参数类
 **/
@Data
public class AuthVO {

    private String accessKey;

    private String secretKey;
}
