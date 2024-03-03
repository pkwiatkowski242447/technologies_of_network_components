package pl.tks.gr3.cinema.exceptions.services.crud.staff;

import pl.tks.gr3.cinema.exceptions.services.GeneralStaffServiceException;

public class StaffServiceDeleteException extends GeneralStaffServiceException {
    public StaffServiceDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
