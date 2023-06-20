package com.spike.configuration;

import com.spike.model.Dependency;

import java.util.Set;

public interface Configuration {
    Set<Dependency> getDependencies();

    Set<Class<?>> getComponents();

    void configure(String configuration);
}
