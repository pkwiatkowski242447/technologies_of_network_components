package pl.tks.gr3.cinema.adapters.exceptions;

public class GeneralRepositoryException extends Exception {
    public GeneralRepositoryException(String message) {
        super(message);
    }

    public GeneralRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
