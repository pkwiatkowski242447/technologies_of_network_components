package pl.tks.gr3.cinema.viewsoap.api;

import pl.tks.gr3.cinema.viewsoap.model.register.*;

public interface UserRegisterEndpointInterface {

    ClientRegisterResponse registerClient(ClientRegisterRequest clientRegisterRequest);
    StaffRegisterResponse registerStaff(StaffRegisterRequest staffRegisterRequest);
    AdminRegisterResponse registerAdmin(AdminRegisterRequest adminRegisterRequest);
}
