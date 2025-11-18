package com.avaya.spring.framework;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class AopBeanPostProcessor implements BeanPostProcessor{

    @Override
     public Object postProcessAfterInitialization(Object bean, String beanName) {

        // Create CGLIB enhancer to generate proxy class
        Enhancer enhancer = new Enhancer();
        // Set the original bean class as superclass
        enhancer.setSuperclass(bean.getClass());
        // Create method interceptor to handle method calls
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                System.out.println("AOP Proxy on \"" + beanName + "\" : Before method " + method.getName() + "() execution");
                // Execute the original method on the target bean
                Object result = method.invoke(bean, objects);
                System.out.println("AOP Proxy on \"" + beanName + "\" :  After method " + method.getName() + "() execution");
                return result;
            }
        });
        // Create and return the proxy instance
        return enhancer.create() ;
    }
}
