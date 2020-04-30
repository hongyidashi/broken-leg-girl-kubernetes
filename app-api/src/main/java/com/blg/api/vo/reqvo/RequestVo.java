package com.blg.api.vo.reqvo;

import lombok.Data;

import java.io.Serializable;

/**
 * http请求的基类
 * panhongtong
 */
@Data
public abstract class RequestVo implements Serializable {
    private String token;
}
