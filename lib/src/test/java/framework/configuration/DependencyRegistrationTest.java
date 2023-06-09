package framework.configuration;

import framework.annotation.Component;
import framework.annotation.Inject;
import framework.exceptions.ComponentNotFoundException;
import framework.exceptions.CycledDependencyException;
import framework.model.Dependency;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static junit.framework.TestCase.assertTrue;

class ExampleClass {

}

class ExampleClass2 {

}

class ExampleClass3 {

}

@Component
class ExampleComponent {

}

class ExampleComponent2 {

    @Inject
    private ExampleComponent exampleComponent;
}

class ExampleComponent3 {
    @Inject
    private ExampleComponent2 exampleComponent2;
}


public class DependencyRegistrationTest {
    private DependencyRegistration dependencyRegistration;

    @Before
    public void setUp() {
        dependencyRegistration = new DependencyRegistration();
    }

    @Test(expected = CycledDependencyException.class)
    public void testCycledRelation() throws CycledDependencyException, ComponentNotFoundException {
        dependencyRegistration.registerRelation(ExampleClass.class, ExampleClass2.class);
        dependencyRegistration.registerRelation(ExampleClass2.class, ExampleClass3.class);
        dependencyRegistration.registerRelation(ExampleClass3.class, ExampleClass.class);
        dependencyRegistration.getAllDependencies();
    }


    @Test(expected = ComponentNotFoundException.class)
    public void testInjectingNonDefinedComponent() throws CycledDependencyException, ComponentNotFoundException {
        dependencyRegistration.registerRelation(ExampleComponent2.class, ExampleComponent3.class);
        dependencyRegistration.getAllDependencies();
    }

    @Test(expected = ComponentNotFoundException.class)
    public void testNormalDependency() throws CycledDependencyException, ComponentNotFoundException {
        dependencyRegistration.registerRelation(ExampleComponent.class, ExampleComponent2.class);
        Set<Dependency> dependencySet = dependencyRegistration.getAllDependencies();
        assertTrue(dependencySet.stream().anyMatch(dependency -> dependency.getClient().equals(ExampleComponent2.class) && dependency.getDependsOn().equals(ExampleComponent.class)));

    }


}
