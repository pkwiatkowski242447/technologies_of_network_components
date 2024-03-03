package pl.tks.gr3.cinema.exceptions.services.crud.staff;

import pl.tks.gr3.cinema.exceptions.services.GeneralStaffServiceException;

public class StaffServiceUpdateException extends GeneralStaffServiceException {
    public StaffServiceUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
