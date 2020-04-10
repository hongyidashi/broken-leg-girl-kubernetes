package com.blg.framework.config;

import com.blg.framework.jdbc.DataSources;
import com.blg.framework.jpa.hibernate.dialect.MySQLDialect;
import com.blg.framework.jpa.hibernate.naming.ImplicitNamingStrategy;
import com.blg.framework.jpa.support.CommentWriter;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Comparator;

/**
 * @Auther: panhongtong
 * @Date: 2020/4/1 09:02
 * @Description:
 */
@Configuration
//配置了扫描hibernate实体和repository的路径
@EnableJpaRepositories(basePackages = "${"+ Constants.APP_BASE_PACKAGE+"}.**.dao")
@EntityScan("${"+ Constants.APP_BASE_PACKAGE+"}.**.model")
//配置扫描组件的路径，这里配置了多个路径
@ComponentScan(basePackages = {"${"+ Constants.APP_BASE_PACKAGE+"},com.blg.api.**"})
public class MainConfig {

    @Bean
    public CommentWriter commentWriter(){
        return new CommentWriter();
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory){
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    @Primary
    public TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager){
        TransactionTemplate transactionTemplate = new TransactionTemplate();
        transactionTemplate.setTransactionManager(transactionManager);
        return transactionTemplate;
    }

    private HikariDataSource createHikariDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.addDataSourceProperty("cachePrepStmts", true);
        dataSource.addDataSourceProperty("prepStmtCacheSize", "250");
        dataSource.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        return dataSource;
    }

    private static final String SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT = "hibernate.dialect";
    private static final String SPRING_JPA_PROPERTIES_HIBERNATE_IMPLICIT_NAMING_STRATEGY = "hibernate.implicit_naming_strategy";

    private PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver();

    @Bean("writeDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.write")
    public HikariDataSource dataSourceWrite() {
        return createHikariDataSource();
    }

    @Bean("readDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.read")
    public HikariDataSource dataSourceRead() {
        return createHikariDataSource();
    }

    @Bean
    @Primary
    @DependsOn({"writeDataSource","readDataSource"})
    public DataSource dataSource(ApplicationContext applicationContext) throws Exception {
        DataSource dataSource = DataSources.readWriteSeparate(applicationContext.getBean("writeDataSource",DataSource.class),applicationContext.getBean("readDataSource",DataSource.class));
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + "sql/*.sql";
        Resource[] resources = pathMatchingResourcePatternResolver.getResources(packageSearchPath);
        Arrays.sort(resources, Comparator.comparing(Resource::getFilename));
        for(Resource resource: resources){
            try(Connection connection = dataSource.getConnection()){
                ScriptUtils.executeSqlScript(connection,resource);
            }
        }
        return dataSource;
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.jpa")
    @Primary
    public JpaProperties jpaProperties() {
        JpaProperties jpaProperties = new JpaProperties();
        return jpaProperties;
    }

    @Value("${spring.application.name}")
    private String appName;

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(DataSource dataSource, JpaProperties jpaProperties) {
        if(!jpaProperties.getProperties().containsKey(SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT)){
            if(DataSources.is(dataSource,"mysql")){
                jpaProperties.getProperties().put(SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT, MySQLDialect.class.getCanonicalName());
            }
        }
        if(!jpaProperties.getProperties().containsKey(SPRING_JPA_PROPERTIES_HIBERNATE_IMPLICIT_NAMING_STRATEGY)){
            jpaProperties.getProperties().put(SPRING_JPA_PROPERTIES_HIBERNATE_IMPLICIT_NAMING_STRATEGY, ImplicitNamingStrategy.class.getCanonicalName());
        }
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        String unitName = appName ==null?"app":appName;
        entityManagerFactoryBean.setPersistenceUnitName(unitName);
        entityManagerFactoryBean.setDataSource(dataSource);
        String basePackage = System.getProperty(Constants.APP_BASE_PACKAGE);
        entityManagerFactoryBean.setPackagesToScan(basePackage);
        entityManagerFactoryBean.setJpaPropertyMap(jpaProperties.getProperties());
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setDatabasePlatform(jpaProperties.getDatabasePlatform());
        entityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        return entityManagerFactoryBean;
    }

}
