package pl.tks.gr3.cinema.exceptions.services.crud.admin;

import pl.tks.gr3.cinema.exceptions.services.GeneralAdminServiceException;

public class AdminServiceDeleteException extends GeneralAdminServiceException {
    public AdminServiceDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
