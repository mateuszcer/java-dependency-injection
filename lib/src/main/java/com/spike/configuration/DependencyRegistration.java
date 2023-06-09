package com.spike.configuration;

import com.spike.annotation.Component;
import com.spike.exceptions.ComponentNotFoundException;
import com.spike.exceptions.CycledDependencyException;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableGraph;
import com.spike.exceptions.MissingImplementationException;
import com.spike.exceptions.QualifierClassNotFoundException;
import com.spike.model.Dependency;

import java.util.HashSet;
import java.util.Set;

public class DependencyRegistration {

    private final MutableGraph<Class<?>> dependencyGraph = GraphBuilder.directed().build();

    private final Set<Class<?>> components = new HashSet<>();

    private final Set<Dependency> dependencies = new HashSet<>();

    public DependencyRegistration() {

    }

    public void registerRelation(Class<?> service, Class<?> client) {
        dependencyGraph.putEdge(service, client);
        dependencies.add(new Dependency(client, service, getImplementation(service)));
    }

    public Set<Dependency> getAllDependencies() {
        if (hasCycledDependencies()) throw new CycledDependencyException();

        if (!hasAllImplementations()) throw new ComponentNotFoundException();

        return dependencies;
    }

    public void registerComponent(Class<?> component) {
        components.add(component);
    }

    private Boolean hasCycledDependencies() {
        return Graphs.hasCycle(dependencyGraph);
    }

    private Boolean hasAllImplementations() {

        return dependencyGraph.edges().stream()
                .allMatch(edge -> edge.source().isAnnotationPresent(Component.class) ||
                        components.stream().anyMatch(component -> edge.source().isAssignableFrom(component)));
    }

    private Class<?> getImplementation(Class<?> parent) {
        if (!parent.isInterface())
            return parent;

        return components.stream()
                .filter(parent::isAssignableFrom)
                .findFirst().orElseThrow(MissingImplementationException::new);
    }

    public void registerQualifiedRelation(Class<?> service, Class<?> client, String qualifier) {
        Class<?> implementation = components.stream()
                .filter(clazz -> clazz.getSimpleName().equals(qualifier))
                .findFirst()
                .orElseThrow(QualifierClassNotFoundException::new);

        dependencyGraph.putEdge(service, client);
        dependencies.add(new Dependency(client, service, implementation));
    }


    public Set<Class<?>> getComponents() {
        return components;
    }
}
