package framework.injection;

import framework.annotation.Inject;
import framework.exceptions.ComponentNotFoundException;
import framework.exceptions.ConstructorNotFoundException;
import framework.exceptions.MissingImplementationException;
import framework.exceptions.MultipleImplementationsException;
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
        dependencySet.forEach(dependency -> {
            try {
                createObject(dependency.client());
            } catch (ConstructorNotFoundException | MissingImplementationException | ComponentNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void processDependencies(Set<Dependency> dependencySet) {
        dependencySet.forEach(dependency -> {
            container.registerDependency(dependency.client(), dependency.service());
            try {
                container.registerImplementation(dependency.service(), dependency.implementation());
            } catch (MultipleImplementationsException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void createObject(Class<?> clazz) throws ConstructorNotFoundException, MissingImplementationException, ComponentNotFoundException {
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
                .map(param -> container.getInstance(getImplementation(param)).orElseThrow(ComponentNotFoundException::new)).toArray();
    }

    private Class<?> getImplementation(Class<?> clazz) throws MissingImplementationException {
        return container.getImplementation(clazz).orElseThrow(MissingImplementationException::new);
    }

    private Constructor<?> resolveConstructor(Class<?> clazz) throws ConstructorNotFoundException {
        Constructor<?> constructor;

        constructor = Arrays.stream(clazz.getDeclaredConstructors())
                .filter(constr -> constr.isAnnotationPresent(Inject.class))
                .findFirst()
                .orElse(Arrays.stream(clazz.getDeclaredConstructors()).findFirst().orElseThrow(ConstructorNotFoundException::new));

        return constructor;
    }

    protected void autowireObject(Object object) throws MissingImplementationException, ComponentNotFoundException {
        for (Field field : object.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(Inject.class))
                continue;

            field.setAccessible(true);
            try {
                Class<?> impl = container.getImplementation(field.getType()).orElseThrow(MissingImplementationException::new);
                field.set(object, container.getInstance(impl).orElseThrow(ComponentNotFoundException::new));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void autowire(Class<?> clazz) {
        Optional<Set<Class<?>>> servicesOpt = container.getDependencies(clazz);
        if (servicesOpt.isEmpty())
            return;

        servicesOpt.get().forEach(service -> {
            try {
                createObject(container.getImplementation(service).orElseThrow(MissingImplementationException::new));
            } catch (ConstructorNotFoundException | MissingImplementationException | ComponentNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public Object getService(Class<?> clazz) throws ComponentNotFoundException {
        return container.getInstance(clazz).orElseThrow(ComponentNotFoundException::new);
    }
}
