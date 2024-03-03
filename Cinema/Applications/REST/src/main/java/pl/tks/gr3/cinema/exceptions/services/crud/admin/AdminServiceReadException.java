package pl.tks.gr3.cinema.exceptions.services.crud.admin;

import pl.tks.gr3.cinema.exceptions.services.GeneralAdminServiceException;

public class AdminServiceReadException extends GeneralAdminServiceException {
    public AdminServiceReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
