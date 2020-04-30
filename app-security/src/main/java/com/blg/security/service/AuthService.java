package com.blg.security.service;

import com.blg.security.model.User;
import com.blg.security.vo.AuthVO;
import org.springframework.stereotype.Service;

/**
 * 认证逻辑
 * @author: panhongtong
 * @create: 2020-04-29 23:08
 **/
@Service
public class AuthService {

    public User auth(AuthVO vo) {
        return new User(1L);
    }
}
