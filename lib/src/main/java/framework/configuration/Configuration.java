package framework.configuration;

import framework.model.Dependency;

import java.util.Set;

public interface Configuration {
    Set<Dependency> getDependencies();

    void configure(String configuration);
}
