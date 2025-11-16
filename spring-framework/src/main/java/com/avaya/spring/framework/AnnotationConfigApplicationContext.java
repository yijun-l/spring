package com.avaya.spring.framework;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AnnotationConfigApplicationContext {

    private final Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
    private final Map<String, Object> singletonObjects = new HashMap<>();

    public AnnotationConfigApplicationContext(Class<?> configClass){
        scan(configClass);

        for (Map.Entry<String, BeanDefinition> beanDefinitionEntry : beanDefinitionMap.entrySet()){
            String beanName = beanDefinitionEntry.getKey();
            BeanDefinition beanDefinition = beanDefinitionEntry.getValue();
            if (beanDefinition.getScope().equals(ScopeType.SINGLETON) && !beanDefinition.isLazy()){
                if (!singletonObjects.containsKey(beanName)) {
                    Object singletonBean = createBean(beanDefinition);
                    singletonObjects.put(beanName, singletonBean);
                }
            }
        }
    }

    public Object createBean(BeanDefinition beanDefinition){
        Class<?> beanClass = beanDefinition.getBeanClass();
        try {
            // Create bean instance using default constructor
            Object instance = beanClass.getDeclaredConstructor().newInstance();

            // Perform dependency injection on all required fields
            for (Field field : beanClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(AutoWired.class) && field.getAnnotation(AutoWired.class).value()) {
                    field.setAccessible(true);
                    field.set(instance, getBean(field.getType()));
                }
            }
            return instance;
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private void scan(Class<?> configClass){
        if (configClass.isAnnotationPresent(ComponentScan.class)) {
            // Extract the base package path configured in the @ComponentScan annotation
            ComponentScan componentScan = configClass.getAnnotation(ComponentScan.class);
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

    public Object getBean(Class<?> beanClass) {
        String beanName = null;
        // Iterate through all bean definitions to find the one matching the target class
        for (Map.Entry<String, BeanDefinition> entry: beanDefinitionMap.entrySet()){
            Class<?> entryClass = entry.getValue().getBeanClass();
            // Use reference equality for exact class match
            if (entryClass == beanClass){
                beanName = entry.getKey();
            }
        }

        if (beanName == null){
            return null;
        }

        // Delegate to the bean name based lookup method
        return getBean(beanName);
    }

    public Object getBean(String beanName) {
        // Check if the bean definition exists
        if (!beanDefinitionMap.containsKey(beanName)){
            return null;
        }
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition.getScope().equals(ScopeType.SINGLETON)){
            // Handle singleton beans - return cached instance or create if needed
            Object singletonBean = singletonObjects.get(beanName);
            if (singletonBean == null) {
                // Lazy initialization: create instance on first access
                singletonBean = createBean(beanDefinition);
                singletonObjects.put(beanName, singletonBean);
            }
            return singletonBean;

        } else {
            // Handle prototype beans - create new instance every time
            return createBean(beanDefinition);
        }
    }
}
