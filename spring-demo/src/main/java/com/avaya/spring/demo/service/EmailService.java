package com.avaya.spring.demo.service;

import com.avaya.spring.framework.*;

@Component
public class EmailService implements InitializingBean, BeanNameAware, ApplicationContextAware {
    @AutoWired
    private UserService userService;
    private String userName;
    private String domainName;
    private String beanName;
    private ApplicationContext applicationContext;

    public EmailService(){

    }

    public void test(){
        userService.test();
        System.out.println(userName + '@' + domainName);
        System.out.println("Bean Name: " + beanName);
        System.out.println("Current Spring Container: " + applicationContext);
    }

    @PostConstruct
    public void init(){
        userName = "liuyijun";
    }

    @Override
    public void afterPropertiesSet() {
        domainName = "avaya.com";
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
