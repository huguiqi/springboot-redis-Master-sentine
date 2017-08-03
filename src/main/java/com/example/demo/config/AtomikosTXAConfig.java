package com.example.demo.config;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.transaction.SystemException;

/**
 * Created by sam on 2017/8/2.
 */
@Configuration
public class AtomikosTXAConfig implements TransactionManagementConfigurer {

    @Bean(name = "atomikosTransactionManager")
    public UserTransactionManager atomikosTransactionManager(){
        UserTransactionManager userTransactionManager = new UserTransactionManager();
        userTransactionManager.setForceShutdown(true);
        return userTransactionManager;
    }

    @Bean(name = "atomikosUserTransaction")
    public UserTransactionImp atomikosUserTransaction(){
        UserTransactionImp atomikosUserTransation =new UserTransactionImp();
        try {
            atomikosUserTransation.setTransactionTimeout(100);
        } catch (SystemException e) {
            e.printStackTrace();
        }
        return atomikosUserTransation;
    }

    @Bean
    public JtaTransactionManager txManager() {
        return new JtaTransactionManager(atomikosUserTransaction(),atomikosTransactionManager());
    }


    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return txManager();
    }
}
