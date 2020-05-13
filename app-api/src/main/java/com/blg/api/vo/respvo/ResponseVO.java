package com.blg.api.vo.respvo;

import com.blg.api.constant.ResponseCode;
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
    private int statusCode = 200;
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

    public static ResponseVO fail(String message, int statusCode) {
        return new ResponseVO(message, statusCode);
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

    public ResponseVO(String message, int statusCode) {
        super();
        this.message = message;
        this.statusCode = statusCode;
    }

}
