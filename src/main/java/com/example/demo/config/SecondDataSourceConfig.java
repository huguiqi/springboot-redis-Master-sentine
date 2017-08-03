package com.example.demo.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Created by sam on 2017/7/30.
 */

@Configuration
@MapperScan(value = "com.example.demo.mapper.second",sqlSessionFactoryRef = "secondSqlSessionFactory")
public class SecondDataSourceConfig {


    @Bean(name = "secondDataSource")
    public DataSource secondDataSource(){
        System.out.println("-------second dataSource-------init");
        AtomikosDataSourceBean dataSourceBean = new AtomikosDataSourceBean();
        dataSourceBean.setXaDataSourceClassName("org.h2.jdbcx.JdbcDataSource");
        Properties pts = new Properties();
        pts.setProperty("url","jdbc:h2:mem:SCDB;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        pts.setProperty("user","sa");
        pts.setProperty("password","");
        dataSourceBean.setXaProperties(pts);
        dataSourceBean.setPoolSize(1);
        dataSourceBean.setMaxPoolSize(3);
        return dataSourceBean;

    }


    @Bean
    public DataSourceInitializer secondInitSql(@Qualifier("secondDataSource") DataSource dataSource){

        return init(dataSource,"secondSchema");
    }

    private DataSourceInitializer init(DataSource dataSource,String schameName){
        DataSourceInitializer dsi = new DataSourceInitializer();
        dsi.setDataSource(dataSource);
        dsi.setDatabasePopulator(new ResourceDatabasePopulator(new ClassPathResource(schameName+".sql")));
        return dsi;
    }

    @Bean(name = "secondSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("secondDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        return sessionFactory.getObject();
    }
}
