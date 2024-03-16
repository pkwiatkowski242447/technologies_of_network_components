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
import pl.tks.gr3.cinema.application_services.exceptions.GeneralServiceException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.staff.StaffServiceStaffNotFoundException;
import pl.tks.gr3.cinema.application_services.services.StaffService;
import pl.tks.gr3.cinema.controllers.interfaces.UserControllerInterface;
import pl.tks.gr3.cinema.domain_model.users.Staff;
import pl.tks.gr3.cinema.domain_model.users.User;
import pl.tks.gr3.cinema.viewrest.output.UserOutputDTO;
import pl.tks.gr3.cinema.viewrest.input.UserUpdateDTO;
import pl.tks.gr3.cinema.application_services.services.JWSService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/staffs")
public class StaffController implements UserControllerInterface<Staff> {

    private final static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private final StaffService staffService;
    private final JWSService jwsService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public StaffController(StaffService staffService, JWSService jwsService, PasswordEncoder passwordEncoder) {
        this.staffService = staffService;
        this.jwsService = jwsService;
        this.passwordEncoder = passwordEncoder;
    }

    @PreAuthorize(value = "hasRole(T(pl.tks.gr3.cinema.domain_model.users.Role).STAFF) || hasRole(T(pl.tks.gr3.cinema.domain_model.users.Role).ADMIN)")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<?> findByUUID(@PathVariable("id") UUID staffID) {
        try {
            Staff staff = this.staffService.findByUUID(staffID);
            UserOutputDTO userOutputDTO = new UserOutputDTO(staff.getUserID(), staff.getUserLogin(), staff.isUserStatusActive());
            return this.generateResponseForDTO(userOutputDTO);
        } catch (StaffServiceStaffNotFoundException exception) {
            return ResponseEntity.notFound().build();
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @PreAuthorize(value = "hasRole(T(pl.tks.gr3.cinema.domain_model.users.Role).STAFF) || hasRole(T(pl.tks.gr3.cinema.domain_model.users.Role).ADMIN)")
    @GetMapping(value = "/login/{login}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<?> findByLogin(@PathVariable("login") String staffLogin) {
        try {
            Staff staff = this.staffService.findByLogin(staffLogin);
            UserOutputDTO userOutputDTO = new UserOutputDTO(staff.getUserID(), staff.getUserLogin(), staff.isUserStatusActive());
            return this.generateResponseForDTO(userOutputDTO);
        } catch (StaffServiceStaffNotFoundException exception) {
            return ResponseEntity.notFound().build();
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @GetMapping(value = "/login/self", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findByLogin() {
        try {
            Staff staff = this.staffService.findByLogin(SecurityContextHolder.getContext().getAuthentication().getName());
            UserOutputDTO userOutputDTO = new UserOutputDTO(staff.getUserID(), staff.getUserLogin(), staff.isUserStatusActive());
            String etagContent = jwsService.generateSignatureForUser(staff);
            return ResponseEntity.ok().header(HttpHeaders.ETAG, etagContent).contentType(MediaType.APPLICATION_JSON).body(userOutputDTO);
        } catch (StaffServiceStaffNotFoundException exception) {
            return ResponseEntity.notFound().build();
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @PreAuthorize(value = "hasRole(T(pl.tks.gr3.cinema.domain_model.users.Role).STAFF) || hasRole(T(pl.tks.gr3.cinema.domain_model.users.Role).ADMIN)")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<?> findAllWithMatchingLogin(@RequestParam("match") String staffLogin) {
        try {
            List<UserOutputDTO> listOfDTOs = this.getListOfUserDTOs(this.staffService.findAllMatchingLogin(staffLogin));
            return this.generateResponseForListOfDTOs(listOfDTOs);
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @PreAuthorize(value = "hasRole(T(pl.tks.gr3.cinema.domain_model.users.Role).STAFF) || hasRole(T(pl.tks.gr3.cinema.domain_model.users.Role).ADMIN)")
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<?> findAll() {
        try {
            List<UserOutputDTO> listOfDTOs = this.getListOfUserDTOs(this.staffService.findAll());
            return this.generateResponseForListOfDTOs(listOfDTOs);
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @PutMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@RequestHeader(value = HttpHeaders.IF_MATCH) String ifMatch, @RequestBody @Valid UserUpdateDTO userUpdateDTO) {
        try {
            String password = userUpdateDTO.getUserPassword() == null ? null : passwordEncoder.encode(userUpdateDTO.getUserPassword());
            Staff staff = new Staff(userUpdateDTO.getUserID(), userUpdateDTO.getUserLogin(), password, userUpdateDTO.isUserStatusActive());
            Set<ConstraintViolation<User>> violationSet = validator.validate(staff);
            List<String> messages = violationSet.stream().map(ConstraintViolation::getMessage).toList();
            if (!violationSet.isEmpty()) {
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(messages);
            }

            if (jwsService.verifyUserSignature(ifMatch.replace("\"", ""), staff)) {
                this.staffService.update(staff);
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
    public ResponseEntity<?> activate(@PathVariable("id") UUID staffID) {
        try {
            this.staffService.activate(staffID);
            return ResponseEntity.noContent().build();
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @PreAuthorize(value = "hasRole(T(pl.tks.gr3.cinema.domain_model.users.Role).ADMIN)")
    @PostMapping(value = "/{id}/deactivate")
    @Override
    public ResponseEntity<?> deactivate(@PathVariable("id") UUID staffID) {
        try {
            this.staffService.deactivate(staffID);
            return ResponseEntity.noContent().build();
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    private List<UserOutputDTO> getListOfUserDTOs(List<Staff> listOfClients) {
        List<UserOutputDTO> listOfDTOs = new ArrayList<>();
        for (Staff staff : listOfClients) {
            listOfDTOs.add(new UserOutputDTO(staff.getUserID(), staff.getUserLogin(), staff.isUserStatusActive()));
        }
        return listOfDTOs;
    }

    private ResponseEntity<?> generateResponseForDTO(UserOutputDTO userOutputDTO) {
        if (userOutputDTO == null) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(userOutputDTO);
        }
    }

    private ResponseEntity<?> generateResponseForListOfDTOs(List<UserOutputDTO> listOfDTOs) {
        if (listOfDTOs.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(listOfDTOs);
        }
    }
}
