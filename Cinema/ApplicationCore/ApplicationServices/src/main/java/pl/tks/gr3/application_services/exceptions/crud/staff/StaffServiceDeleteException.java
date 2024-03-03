package pl.tks.gr3.application_services.exceptions.crud.staff;

import pl.tks.gr3.application_services.exceptions.GeneralStaffServiceException;

public class StaffServiceDeleteException extends GeneralStaffServiceException {
    public StaffServiceDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
