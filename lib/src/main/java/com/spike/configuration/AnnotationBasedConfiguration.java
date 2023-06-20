package com.spike.configuration;

import com.spike.annotation.Component;
import com.spike.annotation.Inject;
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

    public void configure(String classPath) {
        try {
            classPathScanner.findAllAnnotatedFields(Inject.class, classPath)
                    .stream()
                    .filter(field -> field.getDeclaringClass().isAnnotationPresent(Component.class))
                    .forEach(field -> dependencyRegistration.registerRelation(field.getType(), field.getDeclaringClass()));

            classPathScanner.findAllAnnotatedConstructors(Inject.class, classPath)
                    .stream()
                    .filter(constructor -> constructor.getDeclaringClass().isAnnotationPresent(Component.class))
                    .forEach(constructor -> Arrays.stream(constructor.getParameterTypes()).forEach(param -> dependencyRegistration.registerRelation(param, constructor.getDeclaringClass())));

            classPathScanner.findAllAnnotatedClasses(Component.class, classPath)
                    .forEach(dependencyRegistration::registerComponent);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
