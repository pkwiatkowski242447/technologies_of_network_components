//package pl.tks.gr3.cinema.viewsoap.endpoints;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.ws.server.endpoint.annotation.Endpoint;
//import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
//import org.springframework.ws.server.endpoint.annotation.RequestPayload;
//import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
//import pl.tks.gr3.cinema.viewsoap.api.UserLoginService;
//import pl.tks.gr3.cinema.domain_model.users.Admin;
//import pl.tks.gr3.cinema.domain_model.users.Client;
//import pl.tks.gr3.cinema.domain_model.users.Staff;
//import pl.tks.gr3.cinema.ports.userinterface.other.JWTUseCase;
//import pl.tks.gr3.cinema.ports.userinterface.other.LoginUserUseCase;
//import pl.tks.gr3.cinema.viewsoap.model.users.UserInputDTO;
//
//@Endpoint
//public class UserLoginServiceImpl implements UserLoginService {
//
//    public final static String USER_NAMESPACE = "http://viewsoap.adapters.cinema/users";
//
//    private final LoginUserUseCase loginUser;
//    private final JWTUseCase jwtUseCase;
//
//    @Autowired
//    public UserLoginServiceImpl(LoginUserUseCase loginUser,
//                                JWTUseCase jwtUseCase) {
//        this.loginUser = loginUser;
//        this.jwtUseCase = jwtUseCase;
//    }
//
//    @PayloadRoot(namespace = USER_NAMESPACE, localPart = "userInputDTO")
//    @ResponsePayload
//    @Override
//    public String loginClient(@RequestPayload UserInputDTO clientInput) {
//        Client client = this.loginUser.loginClient(clientInput.getLogin(), clientInput.getPassword());
//        return this.jwtUseCase.generateJWTToken(client);
//    }
//
//    @PayloadRoot(namespace = USER_NAMESPACE, localPart = "userInputDTO")
//    @ResponsePayload
//    @Override
//    public String loginStaff(@RequestPayload UserInputDTO staffInput) {
//        Staff staff = this.loginUser.loginStaff(staffInput.getLogin(), staffInput.getPassword());
//        return this.jwtUseCase.generateJWTToken(staff);
//    }
//
//    @PayloadRoot(namespace = USER_NAMESPACE, localPart = "userInputDTO")
//    @ResponsePayload
//    @Override
//    public String loginAdmin(@RequestPayload UserInputDTO adminInput) {
//        Admin admin = this.loginUser.loginAdmin(adminInput.getLogin(), adminInput.getPassword());
//        return this.jwtUseCase.generateJWTToken(admin);
//    }
//}
