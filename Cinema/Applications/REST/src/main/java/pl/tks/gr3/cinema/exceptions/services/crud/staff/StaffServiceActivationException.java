package pl.tks.gr3.cinema.exceptions.services.crud.staff;

import pl.tks.gr3.cinema.exceptions.services.GeneralStaffServiceException;

public class StaffServiceActivationException extends GeneralStaffServiceException {
    public StaffServiceActivationException(String message, Throwable cause) {
        super(message, cause);
    }
}
