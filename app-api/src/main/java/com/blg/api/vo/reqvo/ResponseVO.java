package com.blg.api.vo.reqvo;

import com.blg.api.base.ResponseCode;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 响应VO
 * @author: panhongtong
 * @create: 2020-04-29 23:14
 **/
@Data
@NoArgsConstructor
public class ResponseVO implements Serializable {
    private int code = 200;
    private String message = "";
    private Object data;

    public static ResponseVO ok(Object data) {
        return new ResponseVO(data);
    }

    public static ResponseVO fail() {
        return new ResponseVO(null);
    }

    public static ResponseVO fail(String message) {
        return new ResponseVO(message);
    }

    public static ResponseVO fail(String message, int code) {
        return new ResponseVO(message, code);
    }

    public static ResponseVO failByParam(String message) {
        return new ResponseVO(message, ResponseCode.PARAM_ERROR_CODE.getCode());
    }

    public ResponseVO(Object data) {
        super();
        this.data = data;
    }

    public ResponseVO(String message) {
        super();
        this.message = message;
    }

    public ResponseVO(String message, int code) {
        super();
        this.message = message;
        this.code = code;
    }

}
