package framework.configuration;

import com.spike.annotation.Component;
import com.spike.annotation.Inject;
import com.spike.configuration.DependencyRegistration;
import com.spike.exceptions.ComponentNotFoundException;
import com.spike.exceptions.CycledDependencyException;
import com.spike.model.Dependency;
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


interface ExampleNonImplementedInterface {

}

class ExampleComponentWithNonImplementedDependency {
    @Inject
    private ExampleNonImplementedInterface exampleInterface;
}


interface ExampleImplementedInterface {

}

@Component
class ExampleInterfaceImplementation implements ExampleImplementedInterface {

}

class ExampleComponentWithImplementedDependency {
    @Inject
    private ExampleImplementedInterface exampleInterface;
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

    @Test
    public void testNormalDependency() throws CycledDependencyException, ComponentNotFoundException {
        dependencyRegistration.registerComponent(ExampleComponent.class);
        dependencyRegistration.registerRelation(ExampleComponent.class, ExampleComponent2.class);
        Set<Dependency> dependencySet = dependencyRegistration.getAllDependencies();
        assertTrue(dependencySet.stream().anyMatch(dependency -> dependency.client().equals(ExampleComponent2.class)
                && dependency.service().equals(ExampleComponent.class)
                && dependency.implementation().equals(ExampleComponent.class)));
    }

    @Test(expected = ComponentNotFoundException.class)
    public void testNonPresentImplementation() throws CycledDependencyException, ComponentNotFoundException {
        dependencyRegistration.registerRelation(ExampleNonImplementedInterface.class, ExampleComponentWithNonImplementedDependency.class);
        Set<Dependency> dependencySet = dependencyRegistration.getAllDependencies();
    }

    @Test
    public void testPresentImplementation() throws CycledDependencyException, ComponentNotFoundException {
        dependencyRegistration.registerComponent(ExampleInterfaceImplementation.class);
        dependencyRegistration.registerRelation(ExampleImplementedInterface.class, ExampleComponentWithImplementedDependency.class);
        Set<Dependency> dependencySet = dependencyRegistration.getAllDependencies();
        assertTrue(dependencySet.stream().anyMatch(dependency -> dependency.client().equals(ExampleComponentWithImplementedDependency.class)
                && dependency.service().equals(ExampleImplementedInterface.class)
                && dependency.implementation().equals(ExampleInterfaceImplementation.class)));
    }


}
