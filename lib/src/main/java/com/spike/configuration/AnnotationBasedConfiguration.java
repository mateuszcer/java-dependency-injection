package com.spike.configuration;

import com.spike.annotation.Component;
import com.spike.annotation.Inject;
import com.spike.annotation.Qualifier;
import com.spike.file.ClassPathScanner;
import com.spike.model.Dependency;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

public class AnnotationBasedConfiguration implements Configuration {
    private final ClassPathScanner classPathScanner = new ClassPathScanner();

    private final DependencyRegistration dependencyRegistration = new DependencyRegistration();

    @Override
    public Set<Dependency> getDependencies() {
        return dependencyRegistration.getAllDependencies();
    }

    @Override
    public Set<Class<?>> getComponents() {
        return dependencyRegistration.getComponents();
    }

    public void configure(String classPath) {
        try {
            classPathScanner.findAllAnnotatedClasses(Component.class, classPath)
                    .forEach(dependencyRegistration::registerComponent);

            classPathScanner.findAllAnnotatedFields(Inject.class, classPath)
                    .stream()
                    .filter(field -> field.getDeclaringClass().isAnnotationPresent(Component.class))
                    .forEach(field ->
                    {
                        if (field.isAnnotationPresent(Qualifier.class)) {
                            dependencyRegistration.registerQualifiedRelation(field.getType(), field.getDeclaringClass(), field.getAnnotation(Qualifier.class).value());
                        } else {
                            dependencyRegistration.registerRelation(field.getType(), field.getDeclaringClass());
                        }
                    });


            classPathScanner.findAllAnnotatedConstructors(Inject.class, classPath)
                    .stream()
                    .filter(constructor -> constructor.getDeclaringClass().isAnnotationPresent(Component.class))
                    .forEach(constructor -> Arrays.stream(constructor.getParameterTypes()).forEach(param -> dependencyRegistration.registerRelation(param, constructor.getDeclaringClass())));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
