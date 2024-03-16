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
import pl.tks.gr3.cinema.application_services.exceptions.crud.client.ClientServiceClientNotFoundException;
import pl.tks.gr3.cinema.application_services.services.ClientService;
import pl.tks.gr3.cinema.controllers.interfaces.UserControllerInterface;
import pl.tks.gr3.cinema.domain_model.model.Ticket;
import pl.tks.gr3.cinema.domain_model.model.users.Client;
import pl.tks.gr3.cinema.domain_model.model.users.User;
import pl.tks.gr3.cinema.dto.auth.UserOutputDTO;
import pl.tks.gr3.cinema.dto.auth.UserUpdateDTO;
import pl.tks.gr3.cinema.dto.output.TicketDTO;
import pl.tks.gr3.cinema.security.services.JWSService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/clients")
public class ClientController implements UserControllerInterface<Client> {

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private final ClientService clientService;
    private final JWSService jwsService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ClientController(ClientService clientService, JWSService jwsService, PasswordEncoder passwordEncoder) {
        this.clientService = clientService;
        this.jwsService = jwsService;
        this.passwordEncoder = passwordEncoder;
    }

    @PreAuthorize(value = "hasRole(T(pl.tks.gr3.cinema.domain_model.model.users.Role).STAFF) || hasRole(T(pl.tks.gr3.cinema.domain_model.model.users.Role).ADMIN)")
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<?> findAll() {
        try {
            List<UserOutputDTO> listOfDTOs = this.getListOfUserDTOs(this.clientService.findAll());
            return this.generateResponseForListOfDTOs(listOfDTOs);
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @PreAuthorize(value = "hasRole(T(pl.tks.gr3.cinema.domain_model.model.users.Role).STAFF) || hasRole(T(pl.tks.gr3.cinema.domain_model.model.users.Role).ADMIN)")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<?> findByUUID(@PathVariable("id") UUID clientID) {
        try {
            Client client = this.clientService.findByUUID(clientID);
            UserOutputDTO userOutputDTO = new UserOutputDTO(client.getUserID(), client.getUserLogin(), client.isUserStatusActive());
            return this.generateResponseForDTO(userOutputDTO);
        } catch (ClientServiceClientNotFoundException exception) {
            return ResponseEntity.notFound().build();
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @PreAuthorize(value = "hasRole(T(pl.tks.gr3.cinema.domain_model.model.users.Role).STAFF) || hasRole(T(pl.tks.gr3.cinema.domain_model.model.users.Role).ADMIN)")
    @GetMapping(value = "/login/{login}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<?> findByLogin(@PathVariable("login") String clientLogin) {
        try {
            Client client = this.clientService.findByLogin(clientLogin);
            UserOutputDTO userOutputDTO = new UserOutputDTO(client.getUserID(), client.getUserLogin(), client.isUserStatusActive());
            return this.generateResponseForDTO(userOutputDTO);
        } catch (ClientServiceClientNotFoundException exception) {
            return ResponseEntity.notFound().build();
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @GetMapping(value = "/login/self", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findByLogin() {
        try {
            Client client = this.clientService.findByLogin(SecurityContextHolder.getContext().getAuthentication().getName());
            UserOutputDTO userOutputDTO = new UserOutputDTO(client.getUserID(), client.getUserLogin(), client.isUserStatusActive());
            String etagContent = jwsService.generateSignatureForUser(client);
            return ResponseEntity.ok().header(HttpHeaders.ETAG, etagContent).contentType(MediaType.APPLICATION_JSON).body(userOutputDTO);
        } catch (ClientServiceClientNotFoundException exception) {
            return ResponseEntity.notFound().build();
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @PreAuthorize(value = "hasRole(T(pl.tks.gr3.cinema.domain_model.model.users.Role).STAFF) || hasRole(T(pl.tks.gr3.cinema.domain_model.model.users.Role).ADMIN)")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<?> findAllWithMatchingLogin(@RequestParam("match") String clientLogin) {
        try {
            List<UserOutputDTO> listOfDTOs = this.getListOfUserDTOs(this.clientService.findAllMatchingLogin(clientLogin));
            return this.generateResponseForListOfDTOs(listOfDTOs);
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @PreAuthorize(value = "hasRole(T(pl.tks.gr3.cinema.domain_model.model.users.Role).STAFF)")
    @GetMapping(value = "/{id}/ticket-list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getTicketsForCertainUser(@PathVariable("id") UUID clientID) {
        try {
            List<Ticket> listOfTicketsForAClient = this.clientService.getTicketsForUser(clientID);
            List<TicketDTO> listOfDTOs = new ArrayList<>();
            for (Ticket ticket : listOfTicketsForAClient) {
                listOfDTOs.add(new TicketDTO(ticket.getTicketID(), ticket.getMovieTime(), ticket.getTicketPrice(), ticket.getUserID(), ticket.getMovieID()));
            }
            if (listOfTicketsForAClient.isEmpty()) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(listOfDTOs);
            }
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @GetMapping(value = "/self/ticket-list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getTicketsForCertainUser() {
        try {
            Client client = this.clientService.findByLogin(SecurityContextHolder.getContext().getAuthentication().getName());
            List<Ticket> listOfTicketsForAClient = this.clientService.getTicketsForUser(client.getUserID());
            List<TicketDTO> listOfDTOs = new ArrayList<>();
            for (Ticket ticket : listOfTicketsForAClient) {
                listOfDTOs.add(new TicketDTO(ticket.getTicketID(), ticket.getMovieTime(), ticket.getTicketPrice(), ticket.getUserID(), ticket.getMovieID()));
            }
            if (listOfTicketsForAClient.isEmpty()) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(listOfDTOs);
            }
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @PutMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@RequestHeader(value = HttpHeaders.IF_MATCH) String ifMatch, @RequestBody @Valid UserUpdateDTO userUpdateDTO) {
        try {
            String password = userUpdateDTO.getUserPassword() == null ? null : passwordEncoder.encode(userUpdateDTO.getUserPassword());
            Client client = new Client(userUpdateDTO.getUserID(), userUpdateDTO.getUserLogin(), password, userUpdateDTO.isUserStatusActive());
            Set<ConstraintViolation<User>> violationSet = validator.validate(client);
            List<String> messages = violationSet.stream().map(ConstraintViolation::getMessage).toList();
            if (!violationSet.isEmpty()) {
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(messages);
            }

            if (jwsService.verifyUserSignature(ifMatch.replace("\"", ""), client)) {
                this.clientService.update(client);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("Signature and given object does not match.");
            }
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @PreAuthorize(value = "hasRole(T(pl.tks.gr3.cinema.domain_model.model.users.Role).ADMIN)")
    @PostMapping(value = "/{id}/activate")
    @Override
    public ResponseEntity<?> activate(@PathVariable("id") UUID clientID) {
        try {
            this.clientService.activate(clientID);
            return ResponseEntity.noContent().build();
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @PreAuthorize(value = "hasRole(T(pl.tks.gr3.cinema.domain_model.model.users.Role).ADMIN)")
    @PostMapping(value = "/{id}/deactivate")
    @Override
    public ResponseEntity<?> deactivate(@PathVariable("id") UUID clientID) {
        try {
            this.clientService.deactivate(clientID);
            return ResponseEntity.noContent().build();
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    private List<UserOutputDTO> getListOfUserDTOs(List<Client> listOfClients) {
        List<UserOutputDTO> listOfDTOs = new ArrayList<>();
        for (Client client : listOfClients) {
            listOfDTOs.add(new UserOutputDTO(client.getUserID(), client.getUserLogin(), client.isUserStatusActive()));
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
