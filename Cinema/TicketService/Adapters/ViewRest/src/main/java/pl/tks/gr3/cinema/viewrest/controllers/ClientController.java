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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.tks.gr3.cinema.application_services.exceptions.GeneralServiceException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.client.ClientServiceClientNotFoundException;
import pl.tks.gr3.cinema.application_services.exceptions.crud.client.ClientServiceCreateClientDuplicateLoginException;
import pl.tks.gr3.cinema.viewrest.api.ClientControllerInterface;
import pl.tks.gr3.cinema.domain_model.Ticket;
import pl.tks.gr3.cinema.domain_model.users.Client;
import pl.tks.gr3.cinema.domain_model.users.User;
import pl.tks.gr3.cinema.ports.userinterface.other.JWSUseCase;
import pl.tks.gr3.cinema.ports.userinterface.users.ReadUserUseCase;
import pl.tks.gr3.cinema.ports.userinterface.users.WriteUserUseCase;
import pl.tks.gr3.cinema.viewrest.model.tickets.TicketDTO;
import pl.tks.gr3.cinema.viewrest.model.users.UserInputDTO;
import pl.tks.gr3.cinema.viewrest.model.users.UserOutputDTO;
import pl.tks.gr3.cinema.viewrest.model.users.UserUpdateDTO;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/clients")
public class ClientController implements ClientControllerInterface {

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private final ReadUserUseCase<Client> readClient;
    private final WriteUserUseCase<Client> writeClient;
    private final JWSUseCase jwsService;

    @Autowired
    public ClientController(ReadUserUseCase<Client> readClient,
                            WriteUserUseCase<Client> writeClient,
                            JWSUseCase jwsService) {
        this.readClient = readClient;
        this.writeClient = writeClient;
        this.jwsService = jwsService;
    }

    @PostMapping("/register")
    @Override
    public ResponseEntity<?> create(@RequestBody UserInputDTO userInputDTO) {
        try {
            Client mockClient = new Client(UUID.randomUUID(), userInputDTO.getUserLogin());

            Set<ConstraintViolation<User>> violationSet = validator.validate(mockClient);
            List<String> messages = violationSet.stream().map(ConstraintViolation::getMessage).toList();
            if (!violationSet.isEmpty()) {
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(messages);
            }

            Client client = this.writeClient.create(userInputDTO.getUuid(), userInputDTO.getUserLogin());
            UserOutputDTO userOutputDTO = new UserOutputDTO(client.getUserID(), client.getUserLogin(), client.isUserStatusActive());
            return ResponseEntity.created(URI.create("http://localhost:8000/api/v1/clients/" + userOutputDTO.getUserID().toString())).contentType(MediaType.APPLICATION_JSON).body(userOutputDTO);
        } catch (ClientServiceCreateClientDuplicateLoginException exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT).contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @GetMapping(value = "/login/self", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findByLogin() {
        try {
            Client client = this.readClient.findByLogin(SecurityContextHolder.getContext().getAuthentication().getName());
            UserOutputDTO userOutputDTO = new UserOutputDTO(client.getUserID(), client.getUserLogin(), client.isUserStatusActive());
            String etagContent = jwsService.generateSignatureForUser(client);
            return ResponseEntity.ok().header(HttpHeaders.ETAG, etagContent).contentType(MediaType.APPLICATION_JSON).body(userOutputDTO);
        } catch (ClientServiceClientNotFoundException exception) {
            return ResponseEntity.notFound().build();
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @PreAuthorize(value = "hasRole(T(pl.tks.gr3.cinema.domain_model.users.Role).STAFF)")
    @GetMapping(value = "/{id}/ticket-list", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<?> getTicketsForCertainUser(@PathVariable("id") UUID clientID) {
        try {
            List<Ticket> listOfTicketsForAClient = this.readClient.getTicketsForUser(clientID);
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
    @Override
    public ResponseEntity<?> getTicketsForCertainUser() {
        try {
            Client client = this.readClient.findByLogin(SecurityContextHolder.getContext().getAuthentication().getName());
            List<Ticket> listOfTicketsForAClient = this.readClient.getTicketsForUser(client.getUserID());
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
    @Override
    public ResponseEntity<?> update(@RequestHeader(value = HttpHeaders.IF_MATCH) String ifMatch, @RequestBody @Valid UserUpdateDTO userUpdateDTO) {
        try {
            Client client = new Client(userUpdateDTO.getUserID(), userUpdateDTO.getUserLogin(), userUpdateDTO.isUserStatusActive());
            Set<ConstraintViolation<User>> violationSet = validator.validate(client);
            List<String> messages = violationSet.stream().map(ConstraintViolation::getMessage).toList();
            if (!violationSet.isEmpty()) {
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(messages);
            }

            if (jwsService.verifyUserSignature(ifMatch.replace("\"", ""), client)) {
                this.writeClient.update(client);
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
    public ResponseEntity<?> activate(@PathVariable("id") UUID clientID) {
        try {
            this.writeClient.activate(clientID);
            return ResponseEntity.noContent().build();
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @PostMapping(value = "/{id}/deactivate")
    @Override
    public ResponseEntity<?> deactivate(@PathVariable("id") UUID clientID) {
        try {
            this.writeClient.deactivate(clientID);
            return ResponseEntity.noContent().build();
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }
}
