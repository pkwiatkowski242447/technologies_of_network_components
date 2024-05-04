package pl.tks.gr3.cinema.application_services.exceptions.crud.staff;

import pl.tks.gr3.cinema.application_services.exceptions.GeneralStaffServiceException;

public class StaffServiceCreateException extends GeneralStaffServiceException {

    public StaffServiceCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
