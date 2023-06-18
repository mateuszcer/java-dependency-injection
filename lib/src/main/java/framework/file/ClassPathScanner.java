package framework.file;

import com.google.common.reflect.ClassPath;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class ClassPathScanner {

    public Set<Class<?>> findAllAnnotatedClasses(Class<? extends Annotation> annotation, String packageName) throws IOException {
        return ClassPath.from(ClassLoader.getSystemClassLoader())
                .getAllClasses()
                .stream()
                .filter(clazz -> clazz.getPackageName().startsWith(packageName))
                .map(ClassPath.ClassInfo::load)
                .filter(clazz -> clazz.isAnnotationPresent(annotation))
                .collect(Collectors.toSet());
    }

    public Set<Field> findAllAnnotatedFields(Class<? extends Annotation> annotation, String packageName) throws IOException {
        return ClassPath.from(ClassLoader.getSystemClassLoader())
                .getAllClasses()
                .stream()
                .filter(clazz -> clazz.getPackageName().startsWith(packageName))
                .map(ClassPath.ClassInfo::load)
                .flatMap(
                        clazz -> Arrays.stream(clazz.getDeclaredFields())
                                .filter(field -> field.isAnnotationPresent(annotation)))
                .collect(Collectors.toSet());
    }

    public Set<Constructor<?>> findAllAnnotatedConstructors(Class<? extends Annotation> annotation, String packageName) throws IOException {
        return ClassPath.from(ClassLoader.getSystemClassLoader())
                .getAllClasses()
                .stream()
                .filter(clazz -> clazz.getPackageName().startsWith(packageName))
                .map(ClassPath.ClassInfo::load)
                .flatMap(
                        clazz -> Arrays.stream(clazz.getDeclaredConstructors())
                                .filter(constructor -> constructor.isAnnotationPresent(annotation)))
                .collect(Collectors.toSet());
    }


}
