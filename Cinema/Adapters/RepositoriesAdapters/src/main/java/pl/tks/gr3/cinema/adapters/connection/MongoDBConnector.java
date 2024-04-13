package pl.tks.gr3.cinema.adapters.connection;

import lombok.Getter;
import pl.tks.gr3.cinema.adapters.consts.connection.MongoDBConnectorConstants;
import pl.tks.gr3.cinema.adapters.exceptions.other.MongoDBConnectionException;
import pl.tks.gr3.cinema.adapters.messages.MongoDBConnectorMessages;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MongoDBConnector {

    @Getter
    private static int mongoPort;

    @Getter
    private static String mongoHost;

    @Getter
    private static String mongoDatabase;

    @Getter
    private static String username;

    @Getter
    private static String password;


    public static void readConnectionData() {
        try (InputStream inputStream = MongoDBConnector.class.getClassLoader().getResourceAsStream("mongo.properties")) {
            Properties mongoProperties = new Properties();
            mongoProperties.load(inputStream);

            mongoPort = Integer.parseInt(mongoProperties.getProperty(MongoDBConnectorConstants.PORT));
            mongoHost = mongoProperties.getProperty(MongoDBConnectorConstants.HOST);
            mongoDatabase = mongoProperties.getProperty(MongoDBConnectorConstants.DATABASE_NAME);
            username = mongoProperties.getProperty(MongoDBConnectorConstants.USERNAME);
            password = mongoProperties.getProperty(MongoDBConnectorConstants.PASSWORD);
        } catch (IOException exception) {
            throw new MongoDBConnectionException(MongoDBConnectorMessages.CONNECTION_EXCEPTION);
        }
    }
}
