package framework.configuration;

import framework.model.Dependency;

import java.util.Set;

public interface Configuration {
    void configure();

    Set<Dependency> getDependencies();
}
