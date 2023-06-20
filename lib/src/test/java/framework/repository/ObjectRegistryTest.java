package framework.repository;

import com.spike.exceptions.MultipleImplementationsException;
import com.spike.repository.ObjectRegistry;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.*;

interface ExampleInterface {

}

class ExampleImplementation implements ExampleInterface {

}

class ExampleImplementation2 implements ExampleInterface {

}


public class ObjectRegistryTest {

    private ObjectRegistry objectRegistry;

    @Before
    public void setUp() {
        objectRegistry = new ObjectRegistry();
    }


    @Test
    public void testManagingImplementation() throws MultipleImplementationsException {
        objectRegistry.registerImplementation(ExampleInterface.class, ExampleImplementation.class);
        assertEquals(objectRegistry.getImplementation(ExampleInterface.class).orElseThrow(), ExampleImplementation.class);
    }

    @Test
    public void testEmptyImplementation() {
        assertTrue(objectRegistry.getImplementation(ExampleInterface.class).isEmpty());
    }

    @Test
    public void testManagingObjects() {
        ExampleImplementation exampleImplementation = new ExampleImplementation();
        objectRegistry.registerInstance(ExampleImplementation.class, exampleImplementation);
        assertEquals(objectRegistry.getInstance(ExampleImplementation.class).orElseThrow(), exampleImplementation);
    }

    @Test
    public void testEmptyInstance() {
        assertTrue(objectRegistry.getInstance(ExampleImplementation.class).isEmpty());
    }


}
