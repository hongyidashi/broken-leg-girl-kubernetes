package com.blg.security.controller;

import com.blg.api.vo.reqvo.ResponseVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 描述：
 *
 * @Auther: panhongtong
 * @Date: 2020/4/30 14:54
 */
@RestController
public class TestAuthController {

    @GetMapping("anonymous/testAuth")
    public ResponseVO testAuth() {
        return ResponseVO.ok("请求成功");
    }
}
