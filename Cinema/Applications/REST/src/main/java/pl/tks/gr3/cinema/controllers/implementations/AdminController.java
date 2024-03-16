package pl.tks.gr3.cinema.controllers.implementations;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import pl.tks.gr3.cinema.ports.userinterface.JWSServiceInterface;
import pl.tks.gr3.cinema.viewrest.output.UserOutputDTO;
import pl.tks.gr3.cinema.viewrest.input.UserUpdateDTO;
import pl.tks.gr3.cinema.application_services.exceptions.GeneralServiceException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.admin.AdminServiceAdminNotFoundException;
import pl.tks.gr3.cinema.controllers.interfaces.UserControllerInterface;
import pl.tks.gr3.cinema.domain_model.users.Admin;
import pl.tks.gr3.cinema.domain_model.users.User;
import pl.tks.gr3.cinema.ports.userinterface.UserServiceInterface;

import java.util.*;


@RestController
@RequestMapping("/api/v1/admins")
public class AdminController implements UserControllerInterface<Admin> {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private final UserServiceInterface<Admin> adminService;
    private final JWSServiceInterface jwsService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminController(UserServiceInterface<Admin> adminService, JWSServiceInterface jwsService, PasswordEncoder passwordEncoder) {
        this.adminService = adminService;
        this.jwsService = jwsService;
        this.passwordEncoder = passwordEncoder;
    }

    @PreAuthorize(value = "hasRole(T(pl.tks.gr3.cinema.domain_model.users.Role).ADMIN)")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<?> findByUUID(@PathVariable("id") UUID adminID) {
        try {
            Admin admin = this.adminService.findByUUID(adminID);
            UserOutputDTO userOutputDTO = new UserOutputDTO(admin.getUserID(), admin.getUserLogin(), admin.isUserStatusActive());
            return this.generateResponseEntityForDTO(userOutputDTO);
        } catch (AdminServiceAdminNotFoundException exception) {
            return ResponseEntity.notFound().build();
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @PreAuthorize(value = "hasRole(T(pl.tks.gr3.cinema.domain_model.users.Role).ADMIN)")
    @GetMapping(value = "/login/{login}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<?> findByLogin(@PathVariable("login") String adminLogin) {
        try {
            Admin admin = this.adminService.findByLogin(adminLogin);
            UserOutputDTO userOutputDTO = new UserOutputDTO(admin.getUserID(), admin.getUserLogin(), admin.isUserStatusActive());
            return this.generateResponseEntityForDTO(userOutputDTO);
        } catch (AdminServiceAdminNotFoundException exception) {
            return ResponseEntity.notFound().build();
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @GetMapping(value = "/login/self", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findByLogin() {
        try {
            Admin admin = this.adminService.findByLogin(SecurityContextHolder.getContext().getAuthentication().getName());
            UserOutputDTO userOutputDTO = new UserOutputDTO(admin.getUserID(), admin.getUserLogin(), admin.isUserStatusActive());
            String etagContent = jwsService.generateSignatureForUser(admin);
            return ResponseEntity.ok().header(HttpHeaders.ETAG, etagContent).contentType(MediaType.APPLICATION_JSON).body(userOutputDTO);
        } catch (AdminServiceAdminNotFoundException exception) {
            return ResponseEntity.notFound().build();
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @PreAuthorize(value = "hasRole(T(pl.tks.gr3.cinema.domain_model.users.Role).ADMIN)")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<?> findAllWithMatchingLogin(@RequestParam("match") String adminLogin) {
        try {
            List<UserOutputDTO> listOfDTOs = this.getListOfUserDTOs(this.adminService.findAllMatchingLogin(adminLogin));
            return this.generateResponseEntityForListOfDTOs(listOfDTOs);
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @PreAuthorize(value = "hasRole(T(pl.tks.gr3.cinema.domain_model.users.Role).ADMIN)")
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<?> findAll() {
        try {
            List<Admin> listOfAdmins = this.adminService.findAll();
            List<UserOutputDTO> listOfDTOs = this.getListOfUserDTOs(listOfAdmins);
            return this.generateResponseEntityForListOfDTOs(listOfDTOs);
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @PutMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
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
                this.adminService.update(admin);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("Signature and given object does not match.");
            }
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @PreAuthorize(value = "hasRole(T(pl.tks.gr3.cinema.domain_model.users.Role).ADMIN)")
    @PostMapping(value = "/{id}/activate")
    @Override
    public ResponseEntity<?> activate(@PathVariable("id") UUID adminID) {
        try {
            this.adminService.activate(adminID);
            return ResponseEntity.noContent().build();
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @PreAuthorize(value = "hasRole(T(pl.tks.gr3.cinema.domain_model.users.Role).ADMIN)")
    @PostMapping(value = "/{id}/deactivate")
    @Override
    public ResponseEntity<?> deactivate(@PathVariable("id") UUID adminID) {
        try {
            this.adminService.deactivate(adminID);
            return ResponseEntity.noContent().build();
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    private List<UserOutputDTO> getListOfUserDTOs(List<Admin> listOfAdmins) {
        List<UserOutputDTO> listOfDTOs = new ArrayList<>();
        for (Admin admin : listOfAdmins) {
            listOfDTOs.add(new UserOutputDTO(admin.getUserID(), admin.getUserLogin(), admin.isUserStatusActive()));
        }
        return listOfDTOs;
    }

    private ResponseEntity<?> generateResponseEntityForDTO(UserOutputDTO userOutputDTO) {
        if (userOutputDTO == null) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(userOutputDTO);
        }
    }

    private ResponseEntity<?> generateResponseEntityForListOfDTOs(List<UserOutputDTO> listOfDTOs) {
        if (listOfDTOs.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(listOfDTOs);
        }
    }
}
