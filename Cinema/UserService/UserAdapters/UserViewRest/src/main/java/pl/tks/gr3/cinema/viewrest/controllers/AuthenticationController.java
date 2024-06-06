package pl.tks.gr3.cinema.viewrest.controllers;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.tks.gr3.cinema.adapters.rabbitmq.messages.ClientCreateMessage;
import pl.tks.gr3.cinema.adapters.rabbitmq.publishers.ClientCreatePublisher;
import pl.tks.gr3.cinema.application_services.exceptions.GeneralServiceException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.authentication.login.GeneralAuthenticationLoginException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.authentication.register.AuthenticationServiceUserWithGivenLoginExistsException;
import pl.tks.gr3.cinema.domain_model.Admin;
import pl.tks.gr3.cinema.domain_model.Client;
import pl.tks.gr3.cinema.domain_model.Staff;
import pl.tks.gr3.cinema.domain_model.User;
import pl.tks.gr3.cinema.ports.userinterface.other.JWTUseCase;
import pl.tks.gr3.cinema.ports.userinterface.other.LoginUserUseCase;
import pl.tks.gr3.cinema.ports.userinterface.other.RegisterUserUseCase;
import pl.tks.gr3.cinema.viewrest.model.UserInputDTO;
import pl.tks.gr3.cinema.viewrest.model.UserOutputDTO;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private RegisterUserUseCase registerUser;
    private ClientCreatePublisher clientCreatePublisher;
    private LoginUserUseCase loginUser;
    private JWTUseCase jwtService;

    @Autowired
    public AuthenticationController(RegisterUserUseCase registerUser,
                                    ClientCreatePublisher clientCreatePublisher,
                                    LoginUserUseCase loginUser,
                                    JWTUseCase jwtService) {
        this.registerUser = registerUser;
        this.loginUser = loginUser;
        this.jwtService = jwtService;
        this.clientCreatePublisher = clientCreatePublisher;
    }

    @Counted(value = "authentication.controller.register.client.count", description = "The number of calls to create a new client account")
    @Timed(value = "authentication.controller.register.client.time", description = "Time taken to register user with client access level")
    @PostMapping("/register/client")
    public ResponseEntity<?> registerClient(@RequestBody UserInputDTO userInputDTO) {
        try {
            Client mockClient = new Client(UUID.randomUUID(), userInputDTO.getUserLogin(), userInputDTO.getUserPassword());

            Set<ConstraintViolation<User>> violationSet = validator.validate(mockClient);
            List<String> messages = violationSet.stream().map(ConstraintViolation::getMessage).toList();
            if (!violationSet.isEmpty()) {
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(messages);
            }

            Client client = this.registerUser.registerClient(userInputDTO.getUserLogin(), userInputDTO.getUserPassword());
            clientCreatePublisher.publish(new ClientCreateMessage(client.getUserID(), client.getUserLogin()));

            UserOutputDTO userOutputDTO = new UserOutputDTO(client.getUserID(), client.getUserLogin(), client.isUserStatusActive());
            return ResponseEntity.created(URI.create("http://localhost:8000/api/v1/clients/" + userOutputDTO.getUserID().toString())).contentType(MediaType.APPLICATION_JSON).body(userOutputDTO);
        } catch (AuthenticationServiceUserWithGivenLoginExistsException exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT).contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @Counted(value = "authentication.controller.login.client.count", description = "The number of calls to the method authenticating client accounts")
    @Timed(value = "authentication.controller.login.client.time", description = "Time taken to authenticate as user with client access level")
    @PostMapping(value = "/login/client")
    public ResponseEntity<?> loginClient(@RequestBody UserInputDTO userInputDTO) {
        try {
            Client client = loginUser.loginClient(userInputDTO.getUserLogin(), userInputDTO.getUserPassword());
            if (client.isUserStatusActive()) {
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(jwtService.generateJWTToken(client));
            } else {
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("The account that you want to use is disabled.");
            }
        } catch (GeneralAuthenticationLoginException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @Counted(value = "authentication.controller.register.admin.count", description = "The number of calls to create a new admin account")
    @Timed(value = "authentication.controller.register.admin.time", description = "Time taken to register user with admin access level")
    @PreAuthorize(value = "hasRole(T(pl.tks.gr3.cinema.domain_model.Role).ADMIN)")
    @PostMapping("/register/admin")
    public ResponseEntity<?> registerAdmin(@RequestBody UserInputDTO userInputDTO) {
        try {
            Admin mockAdmin = new Admin(UUID.randomUUID(), userInputDTO.getUserLogin(), userInputDTO.getUserPassword());

            Set<ConstraintViolation<User>> violationSet = validator.validate(mockAdmin);
            List<String> messages = violationSet.stream().map(ConstraintViolation::getMessage).toList();
            if (!violationSet.isEmpty()) {
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(messages);
            }

            Admin admin = this.registerUser.registerAdmin(userInputDTO.getUserLogin(), userInputDTO.getUserPassword());
            UserOutputDTO userOutputDTO = new UserOutputDTO(admin.getUserID(), admin.getUserLogin(), admin.isUserStatusActive());
            return ResponseEntity.created(URI.create("http://localhost:8000/api/v1/clients/" + userOutputDTO.getUserID().toString())).contentType(MediaType.APPLICATION_JSON).body(userOutputDTO);
        } catch (AuthenticationServiceUserWithGivenLoginExistsException exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT).contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @Counted(value = "authentication.controller.login.admin.count", description = "The number of calls to the method authenticating admin accounts")
    @Timed(value = "authentication.controller.login.admin.time", description = "Time taken to authenticate as user with admin access level")
    @PostMapping("/login/admin")
    public ResponseEntity<?> loginAdmin(@RequestBody UserInputDTO userInputDTO) {
        try {
            Admin admin = this.loginUser.loginAdmin(userInputDTO.getUserLogin(), userInputDTO.getUserPassword());
            if (admin.isUserStatusActive()) {
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(jwtService.generateJWTToken(admin));
            } else {
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("The account that you want to use is disabled.");
            }
        } catch (GeneralAuthenticationLoginException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @Counted(value = "authentication.controller.register.staff.count", description = "The number of calls to create a new staff account")
    @Timed(value = "authentication.controller.register.staff.time", description = "Time taken to register user with staff access level")
    @PreAuthorize(value = "hasRole(T(pl.tks.gr3.cinema.domain_model.Role).ADMIN)")
    @PostMapping("/register/staff")
    public ResponseEntity<?> registerStaff(@RequestBody UserInputDTO userInputDTO) {
        try {
            Staff mockStaff = new Staff(UUID.randomUUID(), userInputDTO.getUserLogin(), userInputDTO.getUserPassword());

            Set<ConstraintViolation<User>> violationSet = validator.validate(mockStaff);
            List<String> messages = violationSet.stream().map(ConstraintViolation::getMessage).toList();
            if (!violationSet.isEmpty()) {
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(messages);
            }

            Staff staff = this.registerUser.registerStaff(userInputDTO.getUserLogin(), userInputDTO.getUserPassword());
            UserOutputDTO userOutputDTO = new UserOutputDTO(staff.getUserID(), staff.getUserLogin(), staff.isUserStatusActive());
            return ResponseEntity.created(URI.create("http://localhost:8000/api/v1/clients/" + userOutputDTO.getUserID().toString())).contentType(MediaType.APPLICATION_JSON).body(userOutputDTO);
        } catch (AuthenticationServiceUserWithGivenLoginExistsException exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT).contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @Counted(value = "authentication.controller.login.staff.count", description = "The number of calls to the method authenticating staff accounts")
    @Timed(value = "authentication.controller.login.staff.time", description = "Time taken to authenticate as user with staff access level")
    @PostMapping("/login/staff")
    public ResponseEntity<?> loginStaff(@RequestBody UserInputDTO userInputDTO) {
        try {
            Staff staff = this.loginUser.loginStaff(userInputDTO.getUserLogin(), userInputDTO.getUserPassword());
            if (staff.isUserStatusActive()) {
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(jwtService.generateJWTToken(staff));
            } else {
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("The account that you want to use is disabled.");
            }
        } catch (GeneralAuthenticationLoginException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }
}
