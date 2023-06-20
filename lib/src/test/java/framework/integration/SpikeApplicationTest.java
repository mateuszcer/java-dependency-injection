package framework.integration;


import com.spike.SpikeApplication;
import com.spike.annotation.Component;
import com.spike.annotation.Inject;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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


@Component
class TestComponent2 {

    @Inject
    private TestConstructorInjectionComponent testConstructorInjectionComponent;


    public TestConstructorInjectionComponent getTestConstructorInjectionComponent() {
        return testConstructorInjectionComponent;
    }
}


public class SpikeApplicationTest {
    SpikeApplication spikeApplication;

    @Before
    public void setUp() {
        spikeApplication = SpikeApplication.getInstance(SpikeApplicationTest.class);
        spikeApplication.start();
    }


    @Test
    public void testStandardConfiguration() {
        var testComponent = (TestConstructorInjectionComponent) spikeApplication.getService(TestConstructorInjectionComponent.class);
        var testComponent2 = (TestComponent2) spikeApplication.getService(TestComponent2.class);
        assertTrue(testComponent.getExampleInterface().testMethod());
        assertEquals(testComponent, testComponent2.getTestConstructorInjectionComponent());
    }


}
