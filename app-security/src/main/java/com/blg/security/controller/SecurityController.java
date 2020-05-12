package com.blg.security.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 描述：
 *
 * @author: panhongtong
 * @create: 2020-05-06 16:48
 **/
@RestController
public class SecurityController {

    @ApiOperation(value = "测试swaggerUI")
    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @GetMapping("anonymous/testSC")
    public String testSC() {
        return "OK";
    }
}
