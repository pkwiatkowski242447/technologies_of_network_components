package pl.tks.gr3.cinema.application_services.exceptions.crud.staff;

public class StaffServiceCreateStaffDuplicateLoginException extends StaffServiceCreateException {

    public StaffServiceCreateStaffDuplicateLoginException(String message, Throwable cause) {
        super(message, cause);
    }
}
