package com.blg.api.vo.reqvo;

import lombok.Data;

/**
 * http请求的基类
 * panhongtong
 */
@Data
public abstract class RequestVo {
    private String token;
}
