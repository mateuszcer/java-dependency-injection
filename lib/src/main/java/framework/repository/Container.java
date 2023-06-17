package framework.repository;

import framework.exceptions.MultipleImplementationsException;

import java.util.Optional;

public interface Container {

    void registerImplementation(Class<?> interfaceClass, Class<?> implementation) throws MultipleImplementationsException;

    void registerInstance(Class<?> clazz, Object object);

    Optional<Class<?>> getImplementation(Class<?> interfaceClass);

    Optional<Object> getInstance(Class<?> clazz);

}
