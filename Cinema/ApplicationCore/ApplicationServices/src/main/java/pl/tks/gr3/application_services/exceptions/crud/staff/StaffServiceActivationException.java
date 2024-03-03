package pl.tks.gr3.application_services.exceptions.crud.staff;

import pl.tks.gr3.application_services.exceptions.GeneralStaffServiceException;

public class StaffServiceActivationException extends GeneralStaffServiceException {
    public StaffServiceActivationException(String message, Throwable cause) {
        super(message, cause);
    }
}
