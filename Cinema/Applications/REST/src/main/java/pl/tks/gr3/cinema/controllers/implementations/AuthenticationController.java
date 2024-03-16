package pl.tks.gr3.cinema.controllers.implementations;

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
import pl.tks.gr3.cinema.ports.userinterface.AuthenticationServiceInterface;
import pl.tks.gr3.cinema.ports.userinterface.JWTServiceInterface;
import pl.tks.gr3.cinema.viewrest.input.UserInputDTO;
import pl.tks.gr3.cinema.viewrest.output.UserOutputDTO;
import pl.tks.gr3.cinema.application_services.exceptions.GeneralServiceException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.authentication.login.GeneralAuthenticationLoginException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.authentication.register.AuthenticationServiceUserWithGivenLoginExistsException;
import pl.tks.gr3.cinema.domain_model.users.Admin;
import pl.tks.gr3.cinema.domain_model.users.Client;
import pl.tks.gr3.cinema.domain_model.users.Staff;
import pl.tks.gr3.cinema.domain_model.users.User;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private AuthenticationServiceInterface authenticationService;
    private JWTServiceInterface jwtService;

    @Autowired
    public AuthenticationController(AuthenticationServiceInterface authenticationService, JWTServiceInterface jwtService) {
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register/client")
    public ResponseEntity<?> registerClient(@RequestBody UserInputDTO userInputDTO) {
        try {
            Client mockClient = new Client(UUID.randomUUID(), userInputDTO.getUserLogin(), userInputDTO.getUserPassword());

            Set<ConstraintViolation<User>> violationSet = validator.validate(mockClient);
            List<String> messages = violationSet.stream().map(ConstraintViolation::getMessage).toList();
            if (!violationSet.isEmpty()) {
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(messages);
            }

            Client client = this.authenticationService.registerClient(userInputDTO.getUserLogin(), userInputDTO.getUserPassword());
            UserOutputDTO userOutputDTO = new UserOutputDTO(client.getUserID(), client.getUserLogin(), client.isUserStatusActive());
            return ResponseEntity.created(URI.create("http://localhost:8000/api/v1/clients/" + userOutputDTO.getUserID().toString())).contentType(MediaType.APPLICATION_JSON).body(userOutputDTO);
        } catch (AuthenticationServiceUserWithGivenLoginExistsException exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT).contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @PostMapping(value = "/login/client")
    public ResponseEntity<?> loginClient(@RequestBody UserInputDTO userInputDTO) {
        try {
            Client client = authenticationService.loginClient(userInputDTO.getUserLogin(), userInputDTO.getUserPassword());
            if (client.isUserStatusActive()) {
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(jwtService.generateJWTToken(client));
            } else {
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("The account that you want to use is disabled.");
            }
        } catch (GeneralAuthenticationLoginException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @PreAuthorize(value = "hasRole(T(pl.tks.gr3.cinema.domain_model.users.Role).ADMIN)")
    @PostMapping("/register/admin")
    public ResponseEntity<?> registerAdmin(@RequestBody UserInputDTO userInputDTO) {
        try {
            Admin mockAdmin = new Admin(UUID.randomUUID(), userInputDTO.getUserLogin(), userInputDTO.getUserPassword());

            Set<ConstraintViolation<User>> violationSet = validator.validate(mockAdmin);
            List<String> messages = violationSet.stream().map(ConstraintViolation::getMessage).toList();
            if (!violationSet.isEmpty()) {
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(messages);
            }

            Admin admin = this.authenticationService.registerAdmin(userInputDTO.getUserLogin(), userInputDTO.getUserPassword());
            UserOutputDTO userOutputDTO = new UserOutputDTO(admin.getUserID(), admin.getUserLogin(), admin.isUserStatusActive());
            return ResponseEntity.created(URI.create("http://localhost:8000/api/v1/clients/" + userOutputDTO.getUserID().toString())).contentType(MediaType.APPLICATION_JSON).body(userOutputDTO);
        } catch (AuthenticationServiceUserWithGivenLoginExistsException exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT).contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @PostMapping("/login/admin")
    public ResponseEntity<?> loginAdmin(@RequestBody UserInputDTO userInputDTO) {
        try {
            Admin admin = authenticationService.loginAdmin(userInputDTO.getUserLogin(), userInputDTO.getUserPassword());
            if (admin.isUserStatusActive()) {
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(jwtService.generateJWTToken(admin));
            } else {
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("The account that you want to use is disabled.");
            }
        } catch (GeneralAuthenticationLoginException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @PreAuthorize(value = "hasRole(T(pl.tks.gr3.cinema.domain_model.users.Role).ADMIN)")
    @PostMapping("/register/staff")
    public ResponseEntity<?> registerStaff(@RequestBody UserInputDTO userInputDTO) {
        try {
            Staff mockStaff = new Staff(UUID.randomUUID(), userInputDTO.getUserLogin(), userInputDTO.getUserPassword());

            Set<ConstraintViolation<User>> violationSet = validator.validate(mockStaff);
            List<String> messages = violationSet.stream().map(ConstraintViolation::getMessage).toList();
            if (!violationSet.isEmpty()) {
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(messages);
            }

            Staff staff = this.authenticationService.registerStaff(userInputDTO.getUserLogin(), userInputDTO.getUserPassword());
            UserOutputDTO userOutputDTO = new UserOutputDTO(staff.getUserID(), staff.getUserLogin(), staff.isUserStatusActive());
            return ResponseEntity.created(URI.create("http://localhost:8000/api/v1/clients/" + userOutputDTO.getUserID().toString())).contentType(MediaType.APPLICATION_JSON).body(userOutputDTO);
        } catch (AuthenticationServiceUserWithGivenLoginExistsException exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT).contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @PostMapping("/login/staff")
    public ResponseEntity<?> loginStaff(@RequestBody UserInputDTO userInputDTO) {
        try {
            Staff staff = authenticationService.loginStaff(userInputDTO.getUserLogin(), userInputDTO.getUserPassword());
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
