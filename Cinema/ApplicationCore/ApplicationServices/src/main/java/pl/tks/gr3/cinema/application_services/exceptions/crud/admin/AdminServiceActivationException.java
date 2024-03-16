package pl.tks.gr3.cinema.application_services.exceptions.crud.admin;

import pl.tks.gr3.cinema.application_services.exceptions.GeneralAdminServiceException;

public class AdminServiceActivationException extends GeneralAdminServiceException {
    public AdminServiceActivationException(String message, Throwable cause) {
        super(message, cause);
    }
}
