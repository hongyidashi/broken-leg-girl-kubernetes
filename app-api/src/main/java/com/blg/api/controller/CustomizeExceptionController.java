package com.blg.api.controller;

import com.blg.api.exception.MessageCenterException;
import com.blg.api.vo.respvo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 描述：自定义异常控制器，用于拦截部分常见异常
 * 作者: panhongtong
 * 创建时间: 2020-05-13 16:01
 **/
@ControllerAdvice
@Slf4j
public class CustomizeExceptionController {

    /**
     * 拦截捕捉自定义异常 MessageCenterException.class
     */
    @ResponseBody
    @ExceptionHandler(value = MessageCenterException.class)
    public ResponseVO messageCenterExceptionHandler(MessageCenterException ex) {
        ResponseVO respVO = new ResponseVO();
        respVO.setStatusCode(ex.getStatusCode());
        respVO.setMessage(ex.getMessage());
        if(ex.getException()==null){
            // 若只是单纯的像非空这样的条件判断自定义的异常，则显示ex即可
            log.error(ex.getMessage(), ex);
        }else{
            // 否则若是发生错误，那么需要将具体的异常信息写入
            log.error(ex.getMessage(), ex.getException());
        }
        return respVO;
    }

    /**
     * 拦截所有未被捕获的错误，并默认送出500异常状态码
     */
    @ResponseBody
    @ExceptionHandler(value = Throwable.class)
    public ResponseVO exceptionHandler(Throwable ex){
        ResponseVO respVO = new ResponseVO();
        respVO.setStatusCode(500);
        respVO.setMessage("系统运行异常");
        log.error("系统运行异常", ex);
        return respVO;
    }

    /**
     * 拦截请求参数VO的NotNUll注解产生的异常
     */
    @ResponseBody
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseVO exceptionHandler(MethodArgumentNotValidException ex){
        StringBuilder message = new StringBuilder();
        for (ObjectError e: ex.getBindingResult().getAllErrors()) {
            if(message.length()==0) {
                message.append(e.getDefaultMessage());
            }else{
                message.append(",").append(e.getDefaultMessage());
            }
        }
        ResponseVO respVO = new ResponseVO();
        respVO.setStatusCode(400);
        respVO.setMessage(message.toString());
        log.error(message.toString(), ex);
        return respVO;
    }
}
