package framework.configuration;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableGraph;
import framework.annotation.Component;
import framework.exceptions.ComponentNotFoundException;
import framework.exceptions.CycledDependencyException;
import framework.model.Dependency;

import java.util.Set;
import java.util.stream.Collectors;

public class DependencyRegistration {

    private final MutableGraph<Class<?>> dependencyGraph = GraphBuilder.directed().build();

    public DependencyRegistration() {

    }

    public Set<Dependency> getAllDependencies() throws CycledDependencyException, ComponentNotFoundException {
        validate();
        return dependencyGraph.edges().stream().map(p -> new Dependency(p.target(), p.source())).collect(Collectors.toSet());
    }

    public Set<Class<?>> getAllComponents() {
        return dependencyGraph.nodes().stream().filter(clazz -> clazz.isAnnotationPresent(Component.class)).collect(Collectors.toSet());
    }

    public void registerRelation(Class<?> declaringClass, Class<?> toInject) {
        dependencyGraph.putEdge(declaringClass, toInject);
    }

    public void registerComponent(Class<?> component) {
        dependencyGraph.addNode(component);
    }

    private void validate() throws CycledDependencyException, ComponentNotFoundException {
        if (Graphs.hasCycle(dependencyGraph)) throw new CycledDependencyException();

        if (dependencyGraph.nodes().stream().anyMatch(node -> dependencyGraph.predecessors(node).stream().allMatch(predecessor -> predecessor.isAnnotationPresent(Component.class))))
            throw new ComponentNotFoundException();

    }

}
