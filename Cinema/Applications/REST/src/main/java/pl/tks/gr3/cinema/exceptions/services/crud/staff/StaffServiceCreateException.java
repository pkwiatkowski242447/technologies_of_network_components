package pl.tks.gr3.cinema.exceptions.services.crud.staff;

import pl.tks.gr3.cinema.exceptions.services.GeneralStaffServiceException;

public class StaffServiceCreateException extends GeneralStaffServiceException {

    public StaffServiceCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
