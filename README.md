# Spike - Dependency Injection Framework for Java



Spike is a minimalistic dependency injection framework for Java applications. 






## Usage 

Creating application
```java
class Main {
    public static void main(String[] args) {
        SpikeApplication application = SpikeApplication.getInstance(Main.class);
        application.start();
    }
}
```

Declaring component
```java
@Component
class A {
}
```

Injecting dependency
```java
@Component
class A implements B {
}

interface B {
}

@Component
class C {
    @Inject
    private B dependency;
}
```

Or using constructor injection
```java
@Component
class C {
    private final B dependency;

    @Inject
    public C(B dependency) {
        this.dependency = dependency;
    }
}
```

Handling multiple implementations
```java
class A implements B {
}

class C implements B {
}

@Component
class D {
    @Inject
    @Qualifier("C")
    private B dependency;
}
```

Extracting service
```java
class Main {
    public static void main(String[] args) {
        application.getService(C.class);
    }
}
```
