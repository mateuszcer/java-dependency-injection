package com.spike;

import com.spike.configuration.AnnotationBasedConfiguration;
import com.spike.configuration.Configuration;
import com.spike.injection.Injector;
import com.spike.repository.ObjectRegistry;

import java.time.LocalDate;
import java.util.UUID;

public final class SpikeApplication {

    private final UUID id = UUID.randomUUID();
    private final LocalDate startupDate = LocalDate.now();

    private final Class<?> mainClass;

    private final Configuration configuration;

    private final Injector injector;

    private static SpikeApplication INSTANCE;


    private SpikeApplication(Class<?> mainClass, Configuration configuration) {
        this.mainClass = mainClass;
        this.configuration = configuration;
        this.injector = new Injector(new ObjectRegistry());
    }

    public static SpikeApplication getInstance(Class<?> mainClass) {
        if (INSTANCE == null) {
            INSTANCE = new SpikeApplication(mainClass, new AnnotationBasedConfiguration());
        }

        return INSTANCE;
    }

    public void start() {
        configuration.configure(mainClass.getPackageName());
        injector.createObjects(configuration.getDependencies());
        injector.createComponents(configuration.getComponents());
    }

    public Object getService(Class<?> service) {
        return injector.getService(service);
    }

    public void autowireObject(Object object) {
        injector.autowireObject(object);
    }

    public UUID getId() {
        return id;
    }

    public LocalDate getStartupDate() {
        return startupDate;
    }


}
