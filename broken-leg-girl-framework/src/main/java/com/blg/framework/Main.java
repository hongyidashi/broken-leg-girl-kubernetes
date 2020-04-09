package com.blg.framework;

import com.blg.framework.config.Constants;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class })
public class Main {
    /**
     * 根据指定的基础包名启动
     * @param basePackage
     */
    public static void run(String basePackage,String... args){
        System.setProperty(Constants.APP_BASE_PACKAGE,basePackage);
        System.setProperty("file.encoding","utf-8");
        // 设置基础包名
        new SpringApplicationBuilder(Main.class).web(WebApplicationType.SERVLET).run(args);
    }

    /**
     * 使用默认包启动
     */
    public static void run(String... args){
        try {
            // 获取启动类名
            String baseClassName = Thread.currentThread().getStackTrace()[2].getClassName();
            Class baseClass = Class.forName(baseClassName);
            String basePackage = baseClass.getPackage().getName();
            run(basePackage,args);
        } catch (Throwable e) {
            throw e instanceof RuntimeException?(RuntimeException)e:new RuntimeException(e);
        }
    }
}
