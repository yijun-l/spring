package com.avaya.spring.demo.service;

import com.avaya.spring.framework.AutoWired;
import com.avaya.spring.framework.Component;
import com.avaya.spring.framework.InitializingBean;
import com.avaya.spring.framework.PostConstruct;

@Component
public class EmailService implements InitializingBean {
    @AutoWired
    private UserService userService;
    private String userName;
    private String domainName;

    public EmailService(){

    }

    public void test(){
        userService.test();
        System.out.println(userName + '@' + domainName);
    }

    @PostConstruct
    public void init(){
        userName = "liuyijun";
    }

    @Override
    public void afterPropertiesSet() {
        domainName = "avaya.com";
    }
}
