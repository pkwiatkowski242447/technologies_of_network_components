package pl.tks.gr3.cinema.exceptions.services.crud.admin;

import pl.tks.gr3.cinema.exceptions.services.GeneralAdminServiceException;

public class AdminServiceDeactivationException extends GeneralAdminServiceException {
    public AdminServiceDeactivationException(String message, Throwable cause) {
        super(message, cause);
    }
}
