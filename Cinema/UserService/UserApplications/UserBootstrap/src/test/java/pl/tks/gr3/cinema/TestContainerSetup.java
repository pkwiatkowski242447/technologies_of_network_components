package pl.tks.gr3.cinema;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;
import pl.tks.gr3.cinema.adapters.repositories.UserRepository;

@Component
public class TestContainerSetup {

    public static String connectionString;

    @Autowired
    private UserRepository userRepository;

    @Container
    private static final GenericContainer<?> mongoDBContainer = new GenericContainer<>(DockerImageName.parse("mongo:6.0.2"))
            .withEnv("MONGO_INITDB_ROOT_USERNAME", "admin")
            .withEnv("MONGO_INITDB_ROOT_PASSWORD", "adminpassword")
            .withEnv("MONGO_INITDB_DATABASE", "test-db")
            .withCreateContainerCmdModifier(modifier -> {
                modifier.withName("localhost");
                modifier.withHostName("localhost");
                modifier.withPortBindings(new PortBinding(Ports.Binding.bindPort(27020), new ExposedPort(27017)));
            })
            .withExposedPorts(27017);

    @BeforeAll
    public static void init() {
        mongoDBContainer.start();

        // Thread.sleep(1000 * 10);
        mongoDBContainer.waitingFor(Wait.forListeningPort());
    }
}
