package framework.injection;

import framework.annotation.Component;
import framework.annotation.Inject;
import framework.exceptions.ComponentNotFoundException;
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


class ExampleMissingComponent {
    @Inject
    String field;
}

interface NonImplementedInterface {

}

class ExampleNonImplementedFieldComponent {

    @Inject
    NonImplementedInterface nonImplementedInterface;
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
    public void testNormalInjection() throws MissingImplementationException, ComponentNotFoundException {
        injector.autowireObject(this);
        assertTrue(exampleComponent.testMethod());
        ExampleClient exampleClient = (ExampleClient) injector.getService(ExampleClient.class);
        assertTrue(exampleClient.getExampleComponent().testMethod());
    }

    @Test(expected = ComponentNotFoundException.class)
    public void testNonComponentInjection() throws MissingImplementationException, ComponentNotFoundException {
        ExampleMissingComponent exampleMissingComponent = new ExampleMissingComponent();
        injector.autowireObject(exampleMissingComponent);
    }

    @Test(expected = MissingImplementationException.class)
    public void testNonImplementedInjection() throws MissingImplementationException, ComponentNotFoundException {
        var exampleNonImplementedFieldComponent = new ExampleNonImplementedFieldComponent();
        injector.autowireObject(exampleNonImplementedFieldComponent);
    }

}
