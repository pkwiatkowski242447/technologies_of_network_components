package pl.tks.gr3.cinema.exceptions.services.crud.admin;

import pl.tks.gr3.cinema.exceptions.services.GeneralAdminServiceException;

public class AdminServiceCreateException extends GeneralAdminServiceException {

    public AdminServiceCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
