package pl.tks.gr3.cinema.exceptions.services.crud.staff;

import pl.tks.gr3.cinema.exceptions.services.GeneralStaffServiceException;

public class StaffServiceReadException extends GeneralStaffServiceException {
    public StaffServiceReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
