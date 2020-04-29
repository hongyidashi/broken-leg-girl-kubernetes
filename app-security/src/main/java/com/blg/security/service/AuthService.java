package com.blg.security.service;

import com.blg.security.model.User;
import com.blg.security.vo.AuthVO;
import org.springframework.stereotype.Service;

/**
 * @author: panhongtong
 * @create: 2020-04-29 23:08
 * @description: 认证逻辑
 **/
@Service
public class AuthService {

    public User auth(AuthVO query) {
        return new User(1L);
    }
}
