package com.spike.repository;


import java.util.*;

public class ObjectRegistry implements Container {

    private final Map<Class<?>, Class<?>> implementations = new HashMap<>();
    private final Map<Class<?>, Object> components = new HashMap<>();
    private final Map<Class<?>, Set<Class<?>>> dependencies = new HashMap<>();

    private final Map<String, Class<?>> classNameMap = new HashMap<>();

    @Override
    public void registerImplementation(Class<?> interfaceClass, Class<?> implementation) {
        implementations.put(interfaceClass, implementation);
    }

    @Override
    public void registerInstance(Class<?> clazz, Object object) {
        components.put(clazz, object);
    }

    @Override
    public void registerDependency(Class<?> client, Class<?> service) {
        dependencies.compute(client,
                (key, old) -> {
                    if (old == null)
                        old = new HashSet<>();
                    old.add(service);
                    return old;
                });
    }

    @Override
    public Optional<Class<?>> getImplementation(Class<?> interfaceClass) {
        if (!interfaceClass.isInterface())
            return Optional.of(interfaceClass);

        if (implementations.containsKey(interfaceClass))
            return Optional.of(implementations.get(interfaceClass));
        return Optional.empty();
    }

    @Override
    public Optional<Object> getInstance(Class<?> clazz) {
        if (components.containsKey(clazz))
            return Optional.of(components.get(clazz));
        return Optional.empty();
    }

    @Override
    public Optional<Set<Class<?>>> getDependencies(Class<?> clazz) {
        if (dependencies.containsKey(clazz))
            return Optional.of(dependencies.get(clazz));
        return Optional.empty();
    }

    @Override
    public Boolean containsInstance(Class<?> clazz) {
        return components.containsKey(clazz);
    }

    @Override
    public Optional<Object> getInstance(String className) {
        if (classNameMap.containsKey(className))
            return getInstance(classNameMap.get(className));
        return Optional.empty();
    }

    public void registerClass(Class<?> clazz) {
        classNameMap.put(clazz.getSimpleName(), clazz);
    }
}
