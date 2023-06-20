package framework.configuration.annotationconfiguration;

import com.spike.annotation.Component;
import com.spike.annotation.Inject;
import com.spike.annotation.Qualifier;
import com.spike.configuration.AnnotationBasedConfiguration;
import com.spike.model.Dependency;
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
class TestComponent2 implements TestInterface {
    public TestComponent2() {

    }

    public Boolean testMethod() {
        return true;
    }

}

@Component
class TestConstructorInjectionComponent {

    @Inject
    @Qualifier("TestComponent")
    private TestInterface exampleInterface;


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
        assertEquals(dependency.service(), TestComponent.class);
        assertEquals(dependency.implementation(), TestComponent.class);
    }
}
