package framework.repository;

import framework.exceptions.MultipleImplementationsException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ObjectRegistry implements Container {

    private final Map<Class<?>, Class<?>> implementations = new HashMap<>();
    private final Map<Class<?>, Object> components = new HashMap<>();

    @Override
    public void registerImplementation(Class<?> interfaceClass, Class<?> implementation) throws MultipleImplementationsException {
        if (implementations.containsKey(interfaceClass) && !implementations.get(interfaceClass).equals(implementation))
            throw new MultipleImplementationsException();

        implementations.put(interfaceClass, implementation);
    }

    @Override
    public void registerInstance(Class<?> clazz, Object object) {
        components.put(clazz, object);
    }

    @Override
    public Optional<Class<?>> getImplementation(Class<?> interfaceClass) {
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
}
