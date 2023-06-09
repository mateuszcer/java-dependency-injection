package framework.file;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import static junit.framework.TestCase.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface TestAnnotation {

}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface TestAnnotation2 {

}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface TestFieldAnnotation {

}


@TestAnnotation
class ExampleComponent {

    @TestAnnotation
    public class ExampleNestedComponent {

    }
}

class ExampleNonComponent {

}


class ExampleComponentWithAnnotatedFields {
    @TestFieldAnnotation
    private ExampleNonComponent annotatedField;

    private ExampleComponent nonAnnotatedField;
}

public class ClassPathScannerTest {
    private ClassPathScanner classPathScanner;


    @Before
    public void setUp() {
        classPathScanner = new ClassPathScanner();
    }

    @Test
    public void testComponentAnnotatedClasses() throws IOException {
        Set<Class<?>> annotatedClasses = classPathScanner.findAllAnnotatedClasses(TestAnnotation.class, "framework");
        assertTrue("Check if it includes first component", annotatedClasses.contains(ExampleComponent.class));
        assertTrue("Check if it includes nested component", annotatedClasses.contains(ExampleComponent.ExampleNestedComponent.class));
        assertFalse("Check if it dont include other class", annotatedClasses.contains(ExampleNonComponent.class));
    }

    @Test
    public void testZeroAnnotatedClasses() throws IOException {
        assertEquals("There are no classes with that annotation", 0, classPathScanner.findAllAnnotatedClasses(TestAnnotation2.class, "framework").size());
    }


    @Test
    public void testAnnotatedFields() throws IOException {
        List<Field> annotatedFields = classPathScanner.findAllAnnotatedFields(TestFieldAnnotation.class, "framework");
        assertTrue("There is a field with TestFieldAnnotation called annotatedField", annotatedFields.stream().anyMatch(field -> field.getName().equals("annotatedField")));
        assertEquals("Check if declaring class of annotatedField is ExampleComponentWithAnnotatedFields", annotatedFields.stream().findAny().get().getDeclaringClass(), ExampleComponentWithAnnotatedFields.class);
        assertFalse("nonAnnotatedField is not annotated with this annotation", annotatedFields.stream().anyMatch(field -> field.getName().equals("nonAnnotatedField")));
    }
}
