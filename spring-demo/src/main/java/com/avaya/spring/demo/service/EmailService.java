package com.avaya.spring.demo.service;

import com.avaya.spring.framework.AutoWired;
import com.avaya.spring.framework.Component;

@Component
public class EmailService {
    @AutoWired
    private UserService userService;

    public EmailService(){

    }

    public void test(){
        userService.test();
    }


}
