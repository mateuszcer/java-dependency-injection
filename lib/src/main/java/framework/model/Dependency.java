package framework.model;

public class Dependency {
    private final Class<?> client;

    private final Class<?> dependsOn;

    public Dependency(Class<?> client, Class<?> dependsOn) {
        this.client = client;
        this.dependsOn = dependsOn;
    }
}
