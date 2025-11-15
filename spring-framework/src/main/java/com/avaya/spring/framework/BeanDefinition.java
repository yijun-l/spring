package com.avaya.spring.framework;

import lombok.Data;

@Data
public class BeanDefinition {
    private Class<?> beanClass;
    private ScopeType scope = ScopeType.SINGLETON;
    private boolean lazy = false;

    public BeanDefinition(Class<?> beanClass){
        this.beanClass = beanClass;
    }
}
