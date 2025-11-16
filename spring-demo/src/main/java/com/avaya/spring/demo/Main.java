package com.avaya.spring.demo;

import com.avaya.spring.demo.service.EmailService;
import com.avaya.spring.demo.service.UserService;
import com.avaya.spring.framework.AnnotationConfigApplicationContext;

public class Main {

    public static void main(String[] args){
        // Initialize the Spring container with configuration class
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);

        // Retrieve the UserService bean from the Spring container
        UserService userService = (UserService) applicationContext.getBean("userService");

        // Execute the test method to verify bean functionality
        userService.test();

        EmailService emailService = (EmailService) applicationContext.getBean("emailService");
        emailService.test();
    }
}
