package framework.repository;

import java.util.Optional;
import java.util.Set;

public interface Container {

    void registerImplementation(Class<?> interfaceClass, Class<?> implementation);

    void registerInstance(Class<?> clazz, Object object);

    void registerDependency(Class<?> client, Class<?> service);

    Optional<Class<?>> getImplementation(Class<?> interfaceClass);

    Optional<Object> getInstance(Class<?> clazz);

    Optional<Set<Class<?>>> getDependencies(Class<?> clazz);

    Boolean containsInstance(Class<?> clazz);

}
