package pl.tks.gr3.cinema.adapters.exceptions.other;

public class MongoDBConnectionException extends RuntimeException {

    public MongoDBConnectionException(String message) {
        super(message);
    }
}
