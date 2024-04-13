package pl.tks.gr3.cinema.viewsoap.api;

import pl.tks.gr3.cinema.viewsoap.model.login.*;


public interface UserLoginEndpointInterface {

    ClientLoginResponse loginClient(ClientLoginRequest userInput);
    StaffLoginResponse loginStaff(StaffLoginRequest staffInput);
    AdminLoginResponse loginAdmin(AdminLoginRequest adminInput);
}
