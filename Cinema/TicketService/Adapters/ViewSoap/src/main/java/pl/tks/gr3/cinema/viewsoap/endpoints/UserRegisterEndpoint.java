package pl.tks.gr3.cinema.viewsoap.endpoints;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import pl.tks.gr3.cinema.application_services.exceptions.crud.authentication.register.AuthenticationServiceUserExistsException;
import pl.tks.gr3.cinema.domain_model.users.Admin;
import pl.tks.gr3.cinema.domain_model.users.Client;
import pl.tks.gr3.cinema.domain_model.users.Staff;
import pl.tks.gr3.cinema.ports.userinterface.other.JWTUseCase;
import pl.tks.gr3.cinema.ports.userinterface.other.RegisterUserUseCase;
import pl.tks.gr3.cinema.viewsoap.api.UserRegisterEndpointInterface;
import pl.tks.gr3.cinema.viewsoap.consts.WebServiceConsts;
import pl.tks.gr3.cinema.viewsoap.exceptions.register.DuplicateSoapException;
import pl.tks.gr3.cinema.viewsoap.mappers.UserMapper;
import pl.tks.gr3.cinema.viewsoap.model.register.*;

@Endpoint
public class UserRegisterEndpoint implements UserRegisterEndpointInterface {

    private final RegisterUserUseCase registerUser;
    private final JWTUseCase jwtUseCase;

    @Autowired
    public UserRegisterEndpoint(RegisterUserUseCase registerUser,
                                JWTUseCase jwtUseCase) {
        this.registerUser = registerUser;
        this.jwtUseCase = jwtUseCase;
    }

    @PayloadRoot(namespace = WebServiceConsts.USERS_NAMESPACE, localPart = WebServiceConsts.CLIENT_REGISTER_INPUT)
    @ResponsePayload
    @Override
    public ClientRegisterResponse registerClient(@RequestPayload @Valid ClientRegisterRequest clientRegisterRequest) {
        try {
            Client client = registerUser.registerClient(clientRegisterRequest.getLogin(), clientRegisterRequest.getPassword());
            String jwtToken = jwtUseCase.generateJWTToken(client);
            return UserMapper.toClientRegisterResponse(client, jwtToken);
        } catch (AuthenticationServiceUserExistsException exception) {
            throw new DuplicateSoapException("User with given login already exists.");
        }
    }

    @PayloadRoot(namespace = WebServiceConsts.USERS_NAMESPACE, localPart = WebServiceConsts.STAFF_REGISTER_INPUT)
    @ResponsePayload
    @Override
    public StaffRegisterResponse registerStaff(@RequestPayload @Valid StaffRegisterRequest staffRegisterRequest) {
        try {
            Staff staff = registerUser.registerStaff(staffRegisterRequest.getLogin(), staffRegisterRequest.getPassword());
            String jwtToken = jwtUseCase.generateJWTToken(staff);
            return UserMapper.toStaffRegisterResponse(staff, jwtToken);
        } catch (AuthenticationServiceUserExistsException exception) {
            throw new DuplicateSoapException("User with given login already exists.");
        }
    }

    @PayloadRoot(namespace = WebServiceConsts.USERS_NAMESPACE, localPart = WebServiceConsts.ADMIN_REGISTER_INPUT)
    @ResponsePayload
    @Override
    public AdminRegisterResponse registerAdmin(@RequestPayload @Valid AdminRegisterRequest adminRegisterRequest) {
        try {
            Admin admin = registerUser.registerAdmin(adminRegisterRequest.getLogin(), adminRegisterRequest.getPassword());
            String jwtToken = jwtUseCase.generateJWTToken(admin);
            return UserMapper.toAdminRegisterResponse(admin, jwtToken);
        } catch (AuthenticationServiceUserExistsException exception) {
            throw new DuplicateSoapException("User with given login already exists.");
        }
    }
}
