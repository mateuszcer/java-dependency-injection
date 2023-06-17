package framework.injection;

import framework.annotation.Inject;
import framework.exceptions.ComponentNotFoundException;
import framework.exceptions.ConstructorNotFoundException;
import framework.exceptions.MissingImplementationException;
import framework.model.Dependency;
import framework.repository.Container;

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


    private Constructor<?> resolveConstructor(Class<?> clazz) {
        Constructor<?> constructor;

        constructor = Arrays.stream(clazz.getDeclaredConstructors())
                .filter(constr -> constr.isAnnotationPresent(Inject.class))
                .findFirst()
                .orElse(Arrays.stream(clazz.getDeclaredConstructors()).findFirst().orElseThrow(ConstructorNotFoundException::new));

        return constructor;
    }

    protected void autowireObject(Object object) {
        for (Field field : object.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(Inject.class))
                continue;

            field.setAccessible(true);
            try {
                Class<?> impl = getImplementation(field.getType());
                field.set(object, getInstance(impl));
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
