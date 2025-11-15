package com.avaya.spring.framework;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AnnotationConfigApplicationContext {

    private final Map<String, Class<?>> beanMap = new HashMap<>();

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
                        String beanName = beanClass.getAnnotation(Component.class).value();
                        beanMap.put(beanName, beanClass);
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
        if (beanMap.containsKey(beanName)){
            try {
                return beanMap.get(beanName).getDeclaredConstructor().newInstance();
            } catch (Exception e){
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}
