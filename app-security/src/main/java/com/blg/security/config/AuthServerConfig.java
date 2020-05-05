package com.blg.security.config;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import java.util.HashMap;
import java.util.Map;

/**
 * 描述：授权服务配置
 *
 * @author: panhongtong
 * @create: 2020-05-02 10:51
 **/
@Configuration
@EnableAuthorizationServer
public class AuthServerConfig extends AuthorizationServerConfigurerAdapter {
    // 资源ID
    private static final String SOURCE_ID = "order";
    private static final int ACCESS_TOKEN_TIMER = 60 * 60 * 24;
    private static final int REFRESH_TOKEN_TIMER = 60 * 60 * 24 * 30;

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private ClientDetailsService clientDetailsService;

    @Bean
    public AuthorizationServerTokenServices tokenService() {
        DefaultTokenServices service=new DefaultTokenServices();
        service.setClientDetailsService(clientDetailsService);
        service.setSupportRefreshToken(true);
        service.setTokenStore(tokenStore);
        service.setAccessTokenValiditySeconds(7200);
        return service;
    }

    /**
     * 认证管理器，当设置了密码授权类型的时候，需要
     */
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    /**
     * 客户端详情服务
     * 用来配置客户端详情服务，客户端详情信息在这里进行初始化，
     * 你能够把客户端详情信息写死在这里或者是通过数据库来存储调取详情信息。
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                // 密码模式演示
                .withClient("myapp")
                // 资源列表
                .resourceIds(SOURCE_ID)
                // 该client允许的授权类型
                .authorizedGrantTypes("password", "refresh_token")
                // 该client允许的授权范围
                .scopes("all").
                // 此客户端可以使用的权限，选配
                authorities("ADMIN")
                .secret(new BCryptPasswordEncoder().encode("lxapp"))
                // 令牌有效时间
                .accessTokenValiditySeconds(ACCESS_TOKEN_TIMER)
                .refreshTokenValiditySeconds(REFRESH_TOKEN_TIMER)
                // .and()  如果要配置多个客户端的话可以采用这种方式
                .and()
                .withClient("blgapp")
                .resourceIds("blg")
                .authorizedGrantTypes("authorization_code",
                        "password","client_credentials","implicit","refresh_token")
                .scopes("all")
                .autoApprove(false)
                .secret(new BCryptPasswordEncoder().encode("blg"))
                .redirectUris("http://www.baidu.com")
        ;
    }

    @Autowired
    private AuthorizationCodeServices authorizationCodeServices;

    /**
     * 令牌端点服务
     * 用来配置令牌的访问端点(url)和令牌服务(如何生成令牌、存储等)
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.accessTokenConverter(accessTokenConverter());
        endpoints
                .authenticationManager(authenticationManager)
                .authorizationCodeServices(authorizationCodeServices)
                .tokenServices(tokenService())
                .allowedTokenEndpointRequestMethods(HttpMethod.POST);
    }

    /**
     * 配置授权码存取方式
     * @return
     */
    @Bean
    public AuthorizationCodeServices authorizationCodeServices() {
        return new InMemoryAuthorizationCodeServices();
    }

    /**
     * 用来配置令牌点的安全约束(对url进行约束)
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        // 允许表单认证
        oauthServer.allowFormAuthenticationForClients();
        // tokenkey这个endpoint当使用JwtToken且使用非对称加密时，
        // 资源服务用于获取公钥而开放的，这里指这个 endpoint完全公开
        oauthServer
                .tokenKeyAccess("permitAll()")
                // checkToken这个endpoint完全公开
                .checkTokenAccess("permitAll()");
    }

    // JWT
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter() {
            /***
             * 重写增强token方法,用于自定义一些token总需要封装的信息
             */
            @Override
            public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
                String userName = authentication.getUserAuthentication().getName();
                // 得到用户名，去处理数据库可以拿到当前用户的信息和角色信息（需要传递到服务中用到的信息）
                final Map<String, Object> additionalInformation = new HashMap<>();
                // Map假装用户实体
                Map<String, String> userinfo = new HashMap<>();
                userinfo.put("id", "1");
                userinfo.put("username", "LiaoXiang");
                userinfo.put("qqnum", "438944209");
                userinfo.put("userFlag", "1");
                additionalInformation.put("userinfo", JSON.toJSONString(userinfo));
                ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInformation);
                OAuth2AccessToken enhancedToken = super.enhance(accessToken, authentication);
                return enhancedToken;
            }
        };
        // 测试用,资源服务使用相同的字符达到一个对称加密的效果,生产时候使用RSA非对称加密方式
        accessTokenConverter.setSigningKey("SigningKey");
        return accessTokenConverter;
    }

    /**
     * 令牌的存储策略
     */
    @Bean
    public TokenStore tokenStore() {
        RedisTokenStore tokenStore = new RedisTokenStore(redisConnectionFactory);
        return tokenStore;
    }

}
