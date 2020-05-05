package com.blg.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * 描述：安全认证配置
 *
 * @author: panhongtong
 * @create: 2020-05-02 15:46
 **/
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置这个，以保证在刷新Token时能成功刷新
     */
    @Autowired
    public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception {
        // 配置用户来源于数据库
        // 配置密码加密方式  BCryptPasswordEncoder，添加用户加密的时候请也用这个加密
        auth.userDetailsService(userDetailsService()).passwordEncoder(new BCryptPasswordEncoder());
    }

    /**
     * 使用密码模式要配
     */
    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        // 这里是添加两个用户到内存中去，实际中是从#下面去通过数据库判断用户是否存在
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();

        BCryptPasswordEncoder passwordEncode = new BCryptPasswordEncoder();
        String pwd = passwordEncode.encode("123456");
        manager.createUser(User.withUsername("user_1").password(pwd).authorities("USER").build());
        manager.createUser(User.withUsername("user_2").password(pwd).authorities("USER").build());
        return manager;

        // #####################实际开发中在下面写从数据库获取数据###############################
        // return new UserDetailsService() {
        // @Override
        // public UserDetails loadUserByUsername(String username) throws
        // UsernameNotFoundException {
        // // 通过用户名获取用户信息
        // boolean isUserExist = false;
        // if (isUserExist) {
        // //创建spring security安全用户和对应的权限（从数据库查找）
        // User user = new User("username", "password",
        // AuthorityUtils.createAuthorityList("admin", "manager"));
        // return user;
        // } else {
        // throw new UsernameNotFoundException("用户[" + username + "]不存在");
        // }
        // }
        // };

    }

    /**
     * 安全拦截机制
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http.csrf().disable()
                .requestMatchers()
                .anyRequest()
                .and()
                .authorizeRequests()
                .antMatchers("/oauth/**").permitAll()
                .antMatchers("/login/*").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
        ;
        // @formatter:on
    }

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}
