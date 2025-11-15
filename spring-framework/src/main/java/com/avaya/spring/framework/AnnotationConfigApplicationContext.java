package com.avaya.spring.framework;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AnnotationConfigApplicationContext {

    private final Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

    public AnnotationConfigApplicationContext(Class<?> clazz){
        if (clazz.isAnnotationPresent(ComponentScan.class)) {
            // Extract the base package path configured in the @ComponentScan annotation
            ComponentScan componentScan = clazz.getAnnotation(ComponentScan.class);
            String basePackage = componentScan.value();

            // Convert the package path to a filesystem path using the class loader
            ClassLoader classLoader = this.getClass().getClassLoader();
            URL resource = classLoader.getResource(basePackage.replace('.', '/'));

            try {
                // Convert the URL resource to a filesystem directory object
                File packageDir = new File(resource.getFile());

                // Iterate through all files in the package directory
                for (File classFile : packageDir.listFiles()) {
                    // Construct the fully qualified class name from the file name
                    String className = basePackage + "." + classFile.getName().replace(".class", "");

                    Class<?> beanClass = classLoader.loadClass(className);
                    if (beanClass.isAnnotationPresent(Component.class)) {
                        BeanDefinition beanDefinition = new BeanDefinition(beanClass);
                        if (beanClass.isAnnotationPresent(Scope.class)){
                            beanDefinition.setScope(beanClass.getAnnotation(Scope.class).value());
                        }
                        if (beanClass.isAnnotationPresent(Lazy.class)){
                            beanDefinition.setLazy(beanClass.getAnnotation(Lazy.class).value());
                        }
                        String beanName = beanClass.getAnnotation(Component.class).value();
                        if (beanName.isEmpty()){
                            beanName = beanClass.getSimpleName();
                            beanName = Character.toLowerCase(beanName.charAt(0)) + beanName.substring(1);
                        }
                        beanDefinitionMap.put(beanName, beanDefinition);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Object getBean(Class<?> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public Object getBean(String beanName) {
        if (beanDefinitionMap.containsKey(beanName)){
            try {
                return beanDefinitionMap.get(beanName).getBeanClass().getDeclaredConstructor().newInstance();
            } catch (Exception e){
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}
