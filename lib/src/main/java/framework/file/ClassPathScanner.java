package framework.file;

import com.google.common.reflect.ClassPath;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ClassPathScanner {

    public Set<Class<?>> findAllAnnotatedClasses(Class<? extends Annotation> annotation, String packageName) throws IOException {
        return ClassPath.from(ClassLoader.getSystemClassLoader())
                .getAllClasses()
                .stream()
                .filter(clazz -> clazz.getPackageName().equals(packageName))
                .map(ClassPath.ClassInfo::load)
                .filter(clazz -> clazz.isAnnotationPresent(annotation))
                .collect(Collectors.toSet());
    }

    public List<Field> findAllAnnotatedFields(Class<? extends Annotation> annotation, String packageName) throws IOException {
        return ClassPath.from(ClassLoader.getSystemClassLoader())
                .getAllClasses()
                .stream()
                .filter(clazz -> clazz.getPackageName().equals(packageName))
                .map(ClassPath.ClassInfo::load)
                .flatMap(
                        clazz -> Arrays.stream(clazz.getDeclaredFields())
                                .filter(field -> field.isAnnotationPresent(annotation)))
                .collect(Collectors.toList());
    }

}
