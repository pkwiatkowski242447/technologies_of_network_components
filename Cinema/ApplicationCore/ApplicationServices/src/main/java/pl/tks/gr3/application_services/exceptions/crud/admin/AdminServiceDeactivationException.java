package pl.tks.gr3.application_services.exceptions.crud.admin;

import pl.tks.gr3.application_services.exceptions.GeneralAdminServiceException;

public class AdminServiceDeactivationException extends GeneralAdminServiceException {
    public AdminServiceDeactivationException(String message, Throwable cause) {
        super(message, cause);
    }
}
