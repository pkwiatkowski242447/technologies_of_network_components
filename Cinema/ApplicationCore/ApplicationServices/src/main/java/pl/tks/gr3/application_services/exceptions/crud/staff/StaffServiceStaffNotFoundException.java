package pl.tks.gr3.application_services.exceptions.crud.staff;

public class StaffServiceStaffNotFoundException extends StaffServiceReadException {
    public StaffServiceStaffNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
