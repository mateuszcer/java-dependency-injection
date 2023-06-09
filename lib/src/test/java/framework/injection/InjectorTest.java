package framework.injection;

import com.spike.annotation.Component;
import com.spike.annotation.Inject;
import com.spike.exceptions.ComponentNotFoundException;
import com.spike.exceptions.MissingImplementationException;
import com.spike.injection.Injector;
import com.spike.model.Dependency;
import com.spike.repository.ObjectRegistry;
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


class ExampleConstructorInjectionComponent {

    private final ExampleComponent exampleInterface;

    @Inject
    public ExampleConstructorInjectionComponent(ExampleComponent exampleInterface) {
        this.exampleInterface = exampleInterface;
    }

    public ExampleComponent getExampleInterface() {
        return exampleInterface;
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
        dependencies.add(new Dependency(ExampleConstructorInjectionComponent.class, ExampleInterface.class, ExampleComponent.class));
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

    @Test
    public void testConstructorInjection() throws ComponentNotFoundException {
        var exampleConstructorInjectionComponent = (ExampleConstructorInjectionComponent) injector.getService(ExampleConstructorInjectionComponent.class);
        assertTrue(exampleConstructorInjectionComponent.getExampleInterface().testMethod());
    }

}
