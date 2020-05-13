package com.blg.api.exception;

import com.blg.api.constant.ErrorEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 描述：自定义异常类
 * 作者：panhongtong
 * CustomizeExceptionController
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MessageCenterException extends RuntimeException {
    private String message;
    private Integer statusCode;
    private Throwable exception;

    /**
     * @param ErrorEnum 错误码枚举类
     * @param exception 错误信息
     */
    public MessageCenterException(ErrorEnum errorEnum, Throwable exception) {
        super(errorEnum.getMessage(), exception);//将错误压入错误栈中
        this.message = errorEnum.getMessage();
        this.exception = exception;
        this.statusCode = errorEnum.getCode();
    }

    /**
     * @param errorEnum 错误码枚举类
     */
    public MessageCenterException(ErrorEnum errorEnum) {
        this.message = errorEnum.getMessage();
        this.exception = null;
        this.statusCode = errorEnum.getCode();
    }

    /**
     * @param ErrorEnum 错误码枚举类
     * @param exception 错误信息
     * @param message   自定义错误说明
     */
    public MessageCenterException(ErrorEnum errorEnum, Throwable exception, String message) {
        super(errorEnum.getMessage(), exception);//将错误压入错误栈中
        this.message = message;
        this.exception = exception;
        this.statusCode = errorEnum.getCode();
    }

    /**
     * @param errorEnum 错误码枚举类
     * @param message   自定义错误说明
     */
    public MessageCenterException(ErrorEnum errorEnum, String message) {
        this.message = message;
        this.exception = null;
        this.statusCode = errorEnum.getCode();
    }

}