package pl.tks.gr3.cinema.ports.infrastructure.users;

import pl.tks.gr3.cinema.domain_model.users.Client;

import java.util.UUID;

public interface CreateUserPort {

    Client createClient(UUID clientId, String clientLogin);
}
