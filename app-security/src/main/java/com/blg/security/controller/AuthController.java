package com.blg.security.controller;

import com.blg.api.utils.JWTUtils;
import com.blg.api.vo.reqvo.ResponseVO;
import com.blg.security.model.User;
import com.blg.security.service.AuthService;
import com.blg.security.vo.AuthVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author: panhongtong
 * @create: 2020-04-29 23:00
 * @description: 认证控制器
 **/
@RestController
@RequestMapping(value="/oauth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/token")
    public ResponseVO auth(@RequestBody AuthVO query) throws Exception {
        if (StringUtils.isBlank(query.getAccessKey()) || StringUtils.isBlank(query.getSecretKey())) {
            return ResponseVO.failByParam("accessKey and secretKey not null");
        }
        User user = authService.auth(query);
        if (user == null) {
            return ResponseVO.failByParam("认证失败");
        }
        JWTUtils jwt = JWTUtils.getInstance();
        return ResponseVO.ok(jwt.getToken(user.getId().toString()));
    }

    @GetMapping("/token")
    public ResponseVO oauth(AuthVO query) throws Exception {
        if (StringUtils.isBlank(query.getAccessKey()) || StringUtils.isBlank(query.getSecretKey())) {
            return ResponseVO.failByParam("accessKey and secretKey not null");
        }
        User user = authService.auth(query);
        if (user == null) {
            return ResponseVO.failByParam("认证失败");
        }
        JWTUtils jwt = JWTUtils.getInstance();
        return ResponseVO.ok(jwt.getToken(user.getId().toString()));
    }

}
