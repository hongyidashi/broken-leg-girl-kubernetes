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
 * 认证控制器
 * @author: panhongtong
 * @create: 2020-04-29 23:00
 **/
@RestController
@RequestMapping(value="/oauth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/token")
    public ResponseVO auth(@RequestBody AuthVO vo) throws Exception {
        if (StringUtils.isBlank(vo.getAccessKey()) || StringUtils.isBlank(vo.getSecretKey())) {
            return ResponseVO.failByParam("accessKey and secretKey not null");
        }

        // TODO 认证逻辑待完善
        User user = authService.auth(vo);
        if (user == null) {
            return ResponseVO.failByParam("认证失败");
        }
        JWTUtils jwt = JWTUtils.getInstance();
        return ResponseVO.ok(jwt.getToken(user.getId().toString()));
    }

    @GetMapping("/token")
    public ResponseVO oauth(AuthVO vo) throws Exception {
        if (StringUtils.isBlank(vo.getAccessKey()) || StringUtils.isBlank(vo.getSecretKey())) {
            return ResponseVO.failByParam("accessKey and secretKey not null");
        }

        // TODO 认证逻辑待完善
        User user = authService.auth(vo);
        if (user == null) {
            return ResponseVO.failByParam("认证失败");
        }
        JWTUtils jwt = JWTUtils.getInstance();
        return ResponseVO.ok(jwt.getToken(user.getId().toString()));
    }

}
