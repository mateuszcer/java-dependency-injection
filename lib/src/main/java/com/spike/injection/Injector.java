package com.spike.injection;

import com.spike.annotation.Inject;
import com.spike.annotation.Qualifier;
import com.spike.exceptions.ComponentNotFoundException;
import com.spike.exceptions.ConstructorNotFoundException;
import com.spike.exceptions.MissingImplementationException;
import com.spike.exceptions.QualifierClassNotFoundException;
import com.spike.model.Dependency;
import com.spike.repository.Container;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;


public class Injector {
    private final Container container;

    public Injector(Container container) {
        this.container = container;
    }

    public void createObjects(Set<Dependency> dependencySet) {
        processDependencies(dependencySet);
        dependencySet.forEach(dependency -> createObject(dependency.client()));
    }

    private void processDependencies(Set<Dependency> dependencySet) {
        dependencySet.forEach(dependency -> {
            container.registerDependency(dependency.client(), dependency.service());
            container.registerImplementation(dependency.service(), dependency.implementation());
            container.registerClass(dependency.client());
            container.registerClass(dependency.implementation());
        });
    }

    private void createObject(Class<?> clazz) {
        if (container.containsInstance(clazz))
            return;

        autowire(clazz);

        Constructor<?> constructor = resolveConstructor(clazz);

        Object createdObject;

        Object[] parameters = resolveParameters(constructor);

        try {
            constructor.setAccessible(true);
            createdObject = constructor.newInstance(parameters);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        autowireObject(createdObject);

        container.registerInstance(clazz, createdObject);
    }

    private Object[] resolveParameters(Constructor<?> constructor) {
        return Arrays.stream(constructor.getParameterTypes())
                .map(param -> getInstance(getImplementation(param))).toArray();
    }

    private Class<?> getImplementation(Class<?> clazz) {
        return container.getImplementation(clazz).orElseThrow(MissingImplementationException::new);
    }

    private Object getInstance(Class<?> clazz) {
        return container.getInstance(clazz).orElseThrow(ComponentNotFoundException::new);
    }

    private Object getInstance(String className) {
        return container.getInstance(className).orElseThrow(QualifierClassNotFoundException::new);
    }


    private Constructor<?> resolveConstructor(Class<?> clazz) {
        Constructor<?> constructor;

        constructor = Arrays.stream(clazz.getDeclaredConstructors())
                .filter(constr -> constr.isAnnotationPresent(Inject.class))
                .findFirst()
                .orElse(Arrays.stream(clazz.getDeclaredConstructors()).findFirst().orElseThrow(ConstructorNotFoundException::new));

        return constructor;
    }

    public void autowireObject(Object object) {
        for (Field field : object.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(Inject.class))
                continue;

            field.setAccessible(true);
            try {
                if (field.isAnnotationPresent(Qualifier.class))
                    field.set(object, getInstance(field.getAnnotation(Qualifier.class).value()));
                else {
                    Class<?> impl = getImplementation(field.getType());
                    field.set(object, getInstance(impl));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void autowire(Class<?> clazz) {
        Optional<Set<Class<?>>> servicesOpt = container.getDependencies(clazz);
        if (servicesOpt.isEmpty())
            return;

        servicesOpt.get().forEach(service -> createObject(getImplementation(service)));
    }

    public Object getService(Class<?> clazz) {
        return container.getInstance(clazz).orElseThrow(ComponentNotFoundException::new);
    }
}
