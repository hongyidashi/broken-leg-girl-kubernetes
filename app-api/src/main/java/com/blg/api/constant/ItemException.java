package com.blg.api.constant;

/**
 * 描述：异常枚举类的值
 * 作者：panhongtong
 */
public enum ItemException implements ErrorEnum {

    // 定义异常枚举
    NO_EEMPTY(5001, "测试异常")

    ;

    private String message;
    private Integer code;

    ItemException(Integer code, String message) {
        this.message = message;
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Integer getCode() {
        return code;
    }
}
