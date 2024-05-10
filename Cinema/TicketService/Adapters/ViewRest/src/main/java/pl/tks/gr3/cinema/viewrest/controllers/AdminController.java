package pl.tks.gr3.cinema.viewrest.controllers;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import pl.tks.gr3.cinema.application_services.exceptions.GeneralServiceException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.admin.AdminServiceAdminNotFoundException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.admin.AdminServiceCreateAdminDuplicateLoginException;
import pl.tks.gr3.cinema.ports.userinterface.users.ReadUserUseCase;
import pl.tks.gr3.cinema.viewrest.api.UserControllerInterface;
import pl.tks.gr3.cinema.domain_model.users.Admin;
import pl.tks.gr3.cinema.domain_model.users.User;
import pl.tks.gr3.cinema.ports.userinterface.other.JWSUseCase;
import pl.tks.gr3.cinema.ports.userinterface.users.WriteUserUseCase;
import pl.tks.gr3.cinema.viewrest.model.users.UserInputDTO;
import pl.tks.gr3.cinema.viewrest.model.users.UserOutputDTO;
import pl.tks.gr3.cinema.viewrest.model.users.UserUpdateDTO;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/admins")
public class AdminController implements UserControllerInterface {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private final WriteUserUseCase<Admin> writeAdmin;
    private final ReadUserUseCase<Admin> readAdmin;
    private final JWSUseCase jwsService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminController(WriteUserUseCase<Admin> writeAdmin,
                           ReadUserUseCase<Admin> readAdmin,
                           JWSUseCase jwsService,
                           PasswordEncoder passwordEncoder) {
        this.writeAdmin = writeAdmin;
        this.readAdmin = readAdmin;
        this.jwsService = jwsService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping(value = "/register")
    @Override
    public ResponseEntity<?> create(@RequestBody UserInputDTO userInputDTO) {
        try {
            Admin mockAdmin = new Admin(UUID.randomUUID(), userInputDTO.getUserLogin(), userInputDTO.getUserPassword());

            Set<ConstraintViolation<User>> violationSet = validator.validate(mockAdmin);
            List<String> messages = violationSet.stream().map(ConstraintViolation::getMessage).toList();
            if (!violationSet.isEmpty()) {
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(messages);
            }

            Admin admin = this.writeAdmin.create(userInputDTO.getUserLogin(), userInputDTO.getUserPassword());
            UserOutputDTO userOutputDTO = new UserOutputDTO(admin.getUserID(), admin.getUserLogin(), admin.isUserStatusActive());
            return ResponseEntity.created(URI.create("http://localhost:8000/api/v1/clients/" + userOutputDTO.getUserID().toString())).contentType(MediaType.APPLICATION_JSON).body(userOutputDTO);
        } catch (AdminServiceCreateAdminDuplicateLoginException exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT).contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @GetMapping(value = "/login/self", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findByLogin() {
        try {
            Admin admin = this.readAdmin.findByLogin(SecurityContextHolder.getContext().getAuthentication().getName());
            UserOutputDTO userOutputDTO = new UserOutputDTO(admin.getUserID(), admin.getUserLogin(), admin.isUserStatusActive());
            String etagContent = jwsService.generateSignatureForUser(admin);
            return ResponseEntity.ok().header(HttpHeaders.ETAG, etagContent).contentType(MediaType.APPLICATION_JSON).body(userOutputDTO);
        } catch (AdminServiceAdminNotFoundException exception) {
            return ResponseEntity.notFound().build();
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @PutMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<?> update(@RequestHeader(value = HttpHeaders.IF_MATCH) String ifMatch, @RequestBody @Valid UserUpdateDTO userUpdateDTO) {
        try {
            String password = userUpdateDTO.getUserPassword() == null ? null : passwordEncoder.encode(userUpdateDTO.getUserPassword());
            Admin admin = new Admin(userUpdateDTO.getUserID(), userUpdateDTO.getUserLogin(), password, userUpdateDTO.isUserStatusActive());
            Set<ConstraintViolation<User>> violationSet = validator.validate(admin);
            List<String> messages = violationSet.stream().map(ConstraintViolation::getMessage).toList();
            if (!violationSet.isEmpty()) {
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(messages);
            }

            if (jwsService.verifyUserSignature(ifMatch.replace("\"", ""), admin)) {
                this.writeAdmin.update(admin);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("Signature and given object does not match.");
            }
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @PostMapping(value = "/{id}/activate")
    @Override
    public ResponseEntity<?> activate(@PathVariable("id") UUID adminID) {
        try {
            this.writeAdmin.activate(adminID);
            return ResponseEntity.noContent().build();
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @PostMapping(value = "/{id}/deactivate")
    @Override
    public ResponseEntity<?> deactivate(@PathVariable("id") UUID adminID) {
        try {
            this.writeAdmin.deactivate(adminID);
            return ResponseEntity.noContent().build();
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }
}
