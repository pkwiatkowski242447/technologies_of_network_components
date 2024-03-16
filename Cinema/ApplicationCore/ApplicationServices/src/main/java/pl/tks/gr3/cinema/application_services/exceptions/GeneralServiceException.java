package pl.tks.gr3.cinema.application_services.exceptions;

public class GeneralServiceException extends RuntimeException {

    public GeneralServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
