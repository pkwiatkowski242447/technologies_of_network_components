package pl.tks.gr3.cinema.application_services.exceptions.crud.staff;

import pl.tks.gr3.cinema.application_services.exceptions.GeneralStaffServiceException;

public class StaffServiceReadException extends GeneralStaffServiceException {
    public StaffServiceReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
