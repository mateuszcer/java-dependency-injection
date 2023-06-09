package framework.configuration;

import framework.file.ClassPathScanner;
import framework.model.Dependency;

import java.util.Set;

public class AnnotationBaseConfiguration implements Configuration {
    private final ClassPathScanner classPathScanner;

    private final DependencyRegistration dependencyRegistration;

    public AnnotationBaseConfiguration(ClassPathScanner classPathScanner, DependencyRegistration dependencyRegistration) {
        this.classPathScanner = classPathScanner;
        this.dependencyRegistration = dependencyRegistration;
    }

    @Override
    public void configure() {

    }

    @Override
    public Set<Dependency> getDependencies() {
        return null;
    }
}
