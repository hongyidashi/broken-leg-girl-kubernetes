package com.blg.security.filter;

import com.alibaba.fastjson.JSON;
import com.blg.api.base.ResponseCode;
import com.blg.api.utils.JWTUtils;
import com.blg.api.vo.reqvo.ResponseVO;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 描述：认证过滤器，从请求头中判断认证信息
 *
 * @Auther: panhongtong
 * @Date: 2020/4/30 14:13
 */
public class HttpBasicAuthorizeFilter implements Filter {

    private JWTUtils JWTUTILS = JWTUtils.getInstance();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ServletContext context = filterConfig.getServletContext();
        ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(context);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setCharacterEncoding("UTF-8");
        httpResponse.setContentType("application/json; charset=utf-8");
        //健康检查控制
        String uri = httpRequest.getRequestURI();
        if (healthCheckUri(uri)) {
            if (httpRequest.getQueryString() == null || !httpRequest.getQueryString().equals("token=blgnb")) {
                PrintWriter print = httpResponse.getWriter();
                print.write(JSON.toJSONString(ResponseVO.fail("非法请求【缺少token信息】", ResponseCode.NO_AUTH_CODE.getCode())));
                return;
            }
            chain.doFilter(request, response);
        } else if (passAuthUri(uri)) {
            chain.doFilter(request, response);
        } else {
            //验证TOKEN
            String auth = httpRequest.getHeader("Authorization");
            if (!StringUtils.hasText(auth)) {
                PrintWriter print = httpResponse.getWriter();
                print.write(JSON.toJSONString(ResponseVO.fail("非法请求【缺少Authorization信息】", ResponseCode.NO_AUTH_CODE.getCode())));
                return;
            }
            JWTUtils.JWTResult jwt = JWTUTILS.checkToken(auth);
            if (!jwt.isStatus()) {
                PrintWriter print = httpResponse.getWriter();
                print.write(JSON.toJSONString(ResponseVO.fail(jwt.getMsg(), jwt.getCode())));
                return;
            }
            chain.doFilter(httpRequest, response);
        }

    }

    @Override
    public void destroy() {

    }

    /**
     * 初始化URI
     */
    public HttpBasicAuthorizeFilter() {
        addPass();
    }

    /**
     * 可跳过认证的健康检查的URI
     */
    private static final CopyOnWriteArraySet<String> HEALTHCHECK = new CopyOnWriteArraySet<>();

    /**
     * 手动配置可跳过认证的URI
     */
    private static final CopyOnWriteArraySet<String> PASSURI = new CopyOnWriteArraySet<>();

    /**
     * 配置可跳过认证的URI
     */
    private void addPass() {
        // 健康检查
        HEALTHCHECK.add("/autoconfig");
        HEALTHCHECK.add("/configprops");
        HEALTHCHECK.add("/beans");
        HEALTHCHECK.add("/dump");
        HEALTHCHECK.add("/env");
        HEALTHCHECK.add("/health");
        HEALTHCHECK.add("/info");
        HEALTHCHECK.add("/mappings");
        HEALTHCHECK.add("/metrics");
        HEALTHCHECK.add("/shutdown");
        HEALTHCHECK.add("/trace");

        // 自定义
        PASSURI.add("/anonymous");
    }

    /**
     * 健康检查URI
     * @param uri URI
     */
    private Boolean healthCheckUri(String uri) {
        // 判断是否为健康检查
        return HEALTHCHECK.stream().allMatch(pass -> Objects.equals(uri, pass));
    }

    /**
     * 忽略认证URI
     * @param uri URI
     */
    private Boolean passAuthUri(String uri) {
        return PASSURI.stream().allMatch(uri::startsWith);
    }

}
