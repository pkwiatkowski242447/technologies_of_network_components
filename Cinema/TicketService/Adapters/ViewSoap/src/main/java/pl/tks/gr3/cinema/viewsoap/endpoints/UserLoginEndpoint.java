package pl.tks.gr3.cinema.viewsoap.endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import pl.tks.gr3.cinema.application_services.exceptions.crud.authentication.login.AuthenticationServiceLoginAdminException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.authentication.login.AuthenticationServiceLoginClientException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.authentication.login.AuthenticationServiceLoginStaffException;
import pl.tks.gr3.cinema.viewsoap.api.UserLoginEndpointInterface;
import pl.tks.gr3.cinema.domain_model.users.Admin;
import pl.tks.gr3.cinema.domain_model.users.Client;
import pl.tks.gr3.cinema.domain_model.users.Staff;
import pl.tks.gr3.cinema.ports.userinterface.other.JWTUseCase;
import pl.tks.gr3.cinema.ports.userinterface.other.LoginUserUseCase;
import pl.tks.gr3.cinema.viewsoap.consts.WebServiceConsts;
import pl.tks.gr3.cinema.viewsoap.exceptions.login.AdminLoginSoapException;
import pl.tks.gr3.cinema.viewsoap.exceptions.login.ClientLoginSoapException;
import pl.tks.gr3.cinema.viewsoap.exceptions.login.StaffLoginSoapException;
import pl.tks.gr3.cinema.viewsoap.mappers.UserMapper;
import pl.tks.gr3.cinema.viewsoap.model.login.*;

@Endpoint
public class UserLoginEndpoint implements UserLoginEndpointInterface {

    private final LoginUserUseCase loginUser;
    private final JWTUseCase jwtUseCase;

    @Autowired
    public UserLoginEndpoint(LoginUserUseCase loginUser,
                             JWTUseCase jwtUseCase) {
        this.loginUser = loginUser;
        this.jwtUseCase = jwtUseCase;
    }

    @PayloadRoot(namespace = WebServiceConsts.USERS_NAMESPACE, localPart = WebServiceConsts.CLIENT_LOGIN_INPUT)
    @ResponsePayload
    @Override
    public ClientLoginResponse loginClient(@RequestPayload ClientLoginRequest clientInput) {
        try {
            Client client = this.loginUser.loginClient(clientInput.getLogin(), clientInput.getPassword());
            return UserMapper.toClientLoginResponse(this.jwtUseCase.generateJWTToken(client));
        } catch (AuthenticationServiceLoginClientException | BadCredentialsException exception) {
            throw new ClientLoginSoapException("Authentication failed");
        }
    }

    @PayloadRoot(namespace = WebServiceConsts.USERS_NAMESPACE, localPart = WebServiceConsts.STAFF_LOGIN_INPUT)
    @ResponsePayload
    @Override
    public StaffLoginResponse loginStaff(@RequestPayload StaffLoginRequest staffInput) {
        try {
            Staff staff = this.loginUser.loginStaff(staffInput.getLogin(), staffInput.getPassword());
            return UserMapper.toStaffLoginResponse(this.jwtUseCase.generateJWTToken(staff));
        } catch (AuthenticationServiceLoginStaffException | BadCredentialsException exception) {
            throw new StaffLoginSoapException("Authentication failed");
        }
    }

    @PayloadRoot(namespace = WebServiceConsts.USERS_NAMESPACE, localPart = WebServiceConsts.ADMIN_LOGIN_INPUT)
    @ResponsePayload
    @Override
    public AdminLoginResponse loginAdmin(@RequestPayload AdminLoginRequest adminInput) {
        try {
            Admin admin = this.loginUser.loginAdmin(adminInput.getLogin(), adminInput.getPassword());
            return UserMapper.toAdminLoginResponse(this.jwtUseCase.generateJWTToken(admin));
        } catch (AuthenticationServiceLoginAdminException | BadCredentialsException exception) {
            throw new AdminLoginSoapException("Authentication failed");
        }
    }
}
