package pl.tks.gr3.cinema.adapters.connection;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class DatabaseConfig {

    @Value("${mongo.port}")
    private int portNumber;

    @Value("${mongo.hostname}")
    private String hostName;

    @Value("${mongo.database}")
    private String dbName;

    @Value("${mongo.auth-db}")
    private String authDatabase;

    @Value("${mongo.username}")
    private String userName;

    @Value("${mongo.password}")
    private String password;
}
