package pl.tks.gr3.application_services.exceptions.crud.admin;

import pl.tks.gr3.application_services.exceptions.GeneralAdminServiceException;

public class AdminServiceDeleteException extends GeneralAdminServiceException {
    public AdminServiceDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
