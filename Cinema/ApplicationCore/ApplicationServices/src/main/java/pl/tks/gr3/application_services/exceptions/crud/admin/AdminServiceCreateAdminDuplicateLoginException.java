package pl.tks.gr3.application_services.exceptions.crud.admin;

public class AdminServiceCreateAdminDuplicateLoginException extends AdminServiceCreateException {

    public AdminServiceCreateAdminDuplicateLoginException(String message, Throwable cause) {
        super(message, cause);
    }
}
