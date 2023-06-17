package framework.injection;

import framework.annotation.Component;
import framework.annotation.Inject;
import framework.exceptions.MissingImplementationException;
import framework.model.Dependency;
import framework.repository.ObjectRegistry;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;

import static junit.framework.TestCase.assertTrue;


interface ExampleInterface {

    Boolean testMethod();

}
@Component
class ExampleComponent implements ExampleInterface {
    public ExampleComponent(){

    }

    public Boolean testMethod() {
        return true;
    }

}

@Component
class ExampleClient {
    @Inject
    private ExampleInterface exampleComponent;

    public ExampleInterface getExampleComponent() {
        return exampleComponent;
    }

    public ExampleClient() {

    }
}


public class InjectorTest {

    private Injector injector;

    @Inject
    ExampleInterface exampleComponent;


    @Before
    public void setUp() {
        injector = new Injector(new ObjectRegistry());
        HashSet<Dependency> dependencies = new HashSet<>();
        dependencies.add(new Dependency(ExampleClient.class, ExampleInterface.class, ExampleComponent.class));
        injector.createObjects(dependencies);
    }

    @Test
    public void testNormalInjection() throws MissingImplementationException {
        injector.autowireObject(this);
        assertTrue(exampleComponent.testMethod());
        ExampleClient exampleClient = (ExampleClient) injector.getService(ExampleClient.class);
        assertTrue(exampleClient.getExampleComponent().testMethod());
    }

}
