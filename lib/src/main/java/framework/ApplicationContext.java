package framework;

import java.time.LocalDate;
import java.util.UUID;

public class ApplicationContext {

    private final UUID id = UUID.randomUUID();
    private final LocalDate startupDate = LocalDate.now();

    void start() {

    }


}
