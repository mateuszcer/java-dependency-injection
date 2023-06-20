package com.spike.configuration;

import com.spike.annotation.Component;
import com.spike.exceptions.ComponentNotFoundException;
import com.spike.exceptions.CycledDependencyException;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableGraph;
import com.spike.model.Dependency;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class DependencyRegistration {

    private final MutableGraph<Class<?>> dependencyGraph = GraphBuilder.directed().build();

    private final Set<Class<?>> components = new HashSet<>();

    public DependencyRegistration() {

    }

    public void registerRelation(Class<?> service, Class<?> client) {
        dependencyGraph.putEdge(service, client);
    }

    public Set<Dependency> getAllDependencies() {
        if (hasCycledDependencies()) throw new CycledDependencyException();

        if (!hasAllImplementations()) throw new ComponentNotFoundException();

        return dependencyGraph.edges().stream().map(p -> new Dependency(p.target(), p.source(), getImplementation(p.source()))).collect(Collectors.toSet());
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
        if (parent.isAnnotationPresent(Component.class))
            return parent;

        return components.stream()
                .filter(parent::isAssignableFrom)
                .findFirst().orElseThrow(RuntimeException::new);
    }

}
