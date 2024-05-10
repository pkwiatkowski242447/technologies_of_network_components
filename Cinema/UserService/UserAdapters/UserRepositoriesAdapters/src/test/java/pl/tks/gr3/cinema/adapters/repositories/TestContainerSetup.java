package pl.tks.gr3.cinema.adapters.repositories;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public class TestContainerSetup {

    public static final String MONGODB_USERNAME = "admin";
    public static final String MONGODB_PASSWORD = "adminpassword";
    public static final String DATABASE_NAME = "test-db-user";

    public static UserRepository userRepository;

    public static String connectionString;

    @Container
    private static final GenericContainer<?> mongoDBContainer = new GenericContainer<>(DockerImageName.parse("mongo:6.0.2"))
            .withEnv("MONGO_INITDB_ROOT_USERNAME", "admin")
            .withEnv("MONGO_INITDB_ROOT_PASSWORD", "adminpassword")
            .withEnv("MONGO_INITDB_DATABASE", "test-db")
            .withCreateContainerCmdModifier(modifier -> {
                modifier.withName("mongouser");
                modifier.withHostName("mongouser");
            })
            .withExposedPorts(27017);

    @BeforeAll
    public static void init() {
        mongoDBContainer.start();

        mongoDBContainer.waitingFor(Wait.forListeningPort());
        connectionString = "mongodb://%s:%s".formatted(mongoDBContainer.getHost(), mongoDBContainer.getFirstMappedPort());

        System.out.println(connectionString);
        System.out.println(mongoDBContainer.getContainerName());

        // Thread.sleep(1000000);

        userRepository = new UserRepository(connectionString, MONGODB_USERNAME, MONGODB_PASSWORD, DATABASE_NAME);
    }

    @AfterAll
    public static void destroy() {
        mongoDBContainer.stop();
        mongoDBContainer.close();
    }
}
