package pl.tks.gr3.cinema.application_services.exceptions.crud.admin;

public class AdminServiceAdminNotFoundException extends AdminServiceReadException {
    public AdminServiceAdminNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
