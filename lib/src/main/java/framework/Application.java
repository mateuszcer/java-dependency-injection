package framework;

import framework.configuration.AnnotationBasedConfiguration;
import framework.configuration.Configuration;
import framework.injection.Injector;
import framework.repository.ObjectRegistry;

import java.time.LocalDate;
import java.util.UUID;

public final class Application {

    private final UUID id = UUID.randomUUID();
    private final LocalDate startupDate = LocalDate.now();

    private final Class<?> mainClass;

    private final Configuration configuration;

    private final Injector injector;

    private static Application INSTANCE;


    private Application(Class<?> mainClass, Configuration configuration) {
        this.mainClass = mainClass;
        this.configuration = configuration;
        this.injector = new Injector(new ObjectRegistry());
    }

    public static Application getInstance(Class<?> mainClass) {
        if (INSTANCE == null) {
            INSTANCE = new Application(mainClass, new AnnotationBasedConfiguration());
        }

        return INSTANCE;
    }

    public void start() {
        configuration.configure(mainClass.getPackageName());
        injector.createObjects(configuration.getDependencies());
    }

    public Object getService(Class<?> service) {
        return injector.getService(service);
    }

    public UUID getId() {
        return id;
    }

    public LocalDate getStartupDate() {
        return startupDate;
    }


}
