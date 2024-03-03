package pl.tks.gr3.cinema.exceptions.services.crud.staff;

public class StaffServiceStaffNotFoundException extends StaffServiceReadException {
    public StaffServiceStaffNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
