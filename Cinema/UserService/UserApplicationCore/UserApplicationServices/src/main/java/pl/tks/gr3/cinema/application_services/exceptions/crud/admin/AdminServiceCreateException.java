package pl.tks.gr3.cinema.application_services.exceptions.crud.admin;

import pl.tks.gr3.cinema.application_services.exceptions.GeneralAdminServiceException;

public class AdminServiceCreateException extends GeneralAdminServiceException {

    public AdminServiceCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
