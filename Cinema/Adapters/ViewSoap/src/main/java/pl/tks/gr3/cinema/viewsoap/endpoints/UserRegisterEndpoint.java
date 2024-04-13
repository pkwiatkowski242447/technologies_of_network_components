package pl.tks.gr3.cinema.viewsoap.endpoints;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import pl.tks.gr3.cinema.domain_model.users.Admin;
import pl.tks.gr3.cinema.domain_model.users.Client;
import pl.tks.gr3.cinema.domain_model.users.Staff;
import pl.tks.gr3.cinema.ports.userinterface.other.JWTUseCase;
import pl.tks.gr3.cinema.ports.userinterface.other.RegisterUserUseCase;
import pl.tks.gr3.cinema.viewsoap.api.UserRegisterEndpointInterface;
import pl.tks.gr3.cinema.viewsoap.consts.WebServiceConsts;
import pl.tks.gr3.cinema.viewsoap.mappers.UserMapper;
import pl.tks.gr3.cinema.viewsoap.model.register.*;

@Endpoint
public class UserRegisterEndpoint implements UserRegisterEndpointInterface {

    private RegisterUserUseCase registerUser;
    private JWTUseCase jwtUseCase;

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
        Client client = registerUser.registerClient(clientRegisterRequest.getLogin(), clientRegisterRequest.getPassword());
        String jwtToken = jwtUseCase.generateJWTToken(client);
        return UserMapper.toClientRegisterResponse(client, jwtToken);
    }

    @PayloadRoot(namespace = WebServiceConsts.USERS_NAMESPACE, localPart = WebServiceConsts.STAFF_REGISTER_INPUT)
    @ResponsePayload
    @Override
    public StaffRegisterResponse registerStaff(@RequestPayload @Valid StaffRegisterRequest staffRegisterRequest) {
        Staff staff = registerUser.registerStaff(staffRegisterRequest.getLogin(), staffRegisterRequest.getPassword());
        String jwtToken = jwtUseCase.generateJWTToken(staff);
        return UserMapper.toStaffRegisterResponse(staff, jwtToken);
    }

    @PayloadRoot(namespace = WebServiceConsts.USERS_NAMESPACE, localPart = WebServiceConsts.ADMIN_REGISTER_INPUT)
    @ResponsePayload
    @Override
    public AdminRegisterResponse registerAdmin(@RequestPayload @Valid AdminRegisterRequest adminRegisterRequest) {
        Admin admin = registerUser.registerAdmin(adminRegisterRequest.getLogin(), adminRegisterRequest.getPassword());
        String jwtToken = jwtUseCase.generateJWTToken(admin);
        return UserMapper.toAdminRegisterResponse(admin, jwtToken);
    }
}
