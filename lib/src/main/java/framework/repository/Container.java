package framework.repository;

import framework.exceptions.MultipleImplementationsException;

import java.util.Optional;
import java.util.Set;

public interface Container {

    void registerImplementation(Class<?> interfaceClass, Class<?> implementation) throws MultipleImplementationsException;

    void registerInstance(Class<?> clazz, Object object);

    void registerDependency(Class<?> client, Class<?> service);

    Optional<Class<?>> getImplementation(Class<?> interfaceClass);

    Optional<Object> getInstance(Class<?> clazz);

    Optional<Set<Class<?>>> getDependencies(Class<?> clazz);

}
