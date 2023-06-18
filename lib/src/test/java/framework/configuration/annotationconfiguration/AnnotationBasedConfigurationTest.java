package framework.configuration.annotationconfiguration;

import framework.annotation.Component;
import framework.annotation.Inject;
import framework.configuration.AnnotationBasedConfiguration;
import framework.model.Dependency;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static junit.framework.TestCase.assertEquals;


interface TestInterface {

    Boolean testMethod();

}

@Component
class TestComponent implements TestInterface {
    public TestComponent() {

    }

    public Boolean testMethod() {
        return true;
    }

}

@Component
class TestConstructorInjectionComponent {

    private final TestInterface exampleInterface;

    @Inject
    public TestConstructorInjectionComponent(TestInterface exampleInterface) {
        this.exampleInterface = exampleInterface;
    }

    public TestInterface getExampleInterface() {
        return exampleInterface;
    }

}

public class AnnotationBasedConfigurationTest {
    private AnnotationBasedConfiguration annotationBasedConfiguration;

    @Before
    public void setUp() {
        annotationBasedConfiguration = new AnnotationBasedConfiguration();
    }

    @Test
    public void testNormalConfiguration() {
        annotationBasedConfiguration.configure(this.getClass().getPackageName());
        Set<Dependency> dependencySet = annotationBasedConfiguration.getDependencies();
        Dependency dependency = dependencySet.stream().findFirst().orElseThrow();
        assertEquals(dependency.client(), TestConstructorInjectionComponent.class);
        assertEquals(dependency.service(), TestInterface.class);
        assertEquals(dependency.implementation(), TestComponent.class);
    }
}
