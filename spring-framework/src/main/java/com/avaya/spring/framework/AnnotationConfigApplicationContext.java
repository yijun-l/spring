package com.avaya.spring.framework;

public class AnnotationConfigApplicationContext {

    public AnnotationConfigApplicationContext(Class<?> clazz){

    }

    public Object getBean(Class<?> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
