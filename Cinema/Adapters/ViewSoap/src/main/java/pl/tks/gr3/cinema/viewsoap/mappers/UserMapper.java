package pl.tks.gr3.cinema.viewsoap.mappers;

import pl.tks.gr3.cinema.domain_model.users.Admin;
import pl.tks.gr3.cinema.domain_model.users.Client;
import pl.tks.gr3.cinema.domain_model.users.Staff;
import pl.tks.gr3.cinema.domain_model.users.User;
import pl.tks.gr3.cinema.viewsoap.model.login.AdminLoginResponse;
import pl.tks.gr3.cinema.viewsoap.model.login.ClientLoginResponse;
import pl.tks.gr3.cinema.viewsoap.model.login.StaffLoginResponse;
import pl.tks.gr3.cinema.viewsoap.model.register.AdminRegisterResponse;
import pl.tks.gr3.cinema.viewsoap.model.register.ClientRegisterResponse;
import pl.tks.gr3.cinema.viewsoap.model.register.StaffRegisterResponse;
import pl.tks.gr3.cinema.viewsoap.model.register.UserOutputElement;

public class UserMapper {

    public static ClientLoginResponse toClientLoginResponse(String accessToken) {
        return new ClientLoginResponse(accessToken);
    }

    public static StaffLoginResponse toStaffLoginResponse(String accessToken) {
        return new StaffLoginResponse(accessToken);
    }

    public static AdminLoginResponse toAdminLoginResponse(String accessToken) {
        return new AdminLoginResponse(accessToken);
    }

    public static ClientRegisterResponse toClientRegisterResponse(Client client, String accessToken) {
        return new ClientRegisterResponse(UserMapper.toUserOutputElement(client), accessToken);
    }

    public static StaffRegisterResponse toStaffRegisterResponse(Staff staff, String accessToken) {
        return new StaffRegisterResponse(UserMapper.toUserOutputElement(staff), accessToken);
    }

    public static AdminRegisterResponse toAdminRegisterResponse(Admin admin, String accessToken) {
        return new AdminRegisterResponse(UserMapper.toUserOutputElement(admin), accessToken);
    }

    private static UserOutputElement toUserOutputElement(User user) {
        return new UserOutputElement(user.getUserID().toString(), user.getUserLogin(), user.isUserStatusActive());
    }
}
