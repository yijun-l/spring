package com.avaya.spring.demo.service;

import com.avaya.spring.framework.BeanPostProcessor;
import com.avaya.spring.framework.Component;

@Component
public class CustomBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)  {
        System.out.println("Custom preprocessing on creating \"" + beanName + "\"");
        return bean;
    }
}
