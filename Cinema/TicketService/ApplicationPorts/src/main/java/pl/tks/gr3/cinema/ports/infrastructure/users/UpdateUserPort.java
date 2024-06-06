package pl.tks.gr3.cinema.ports.infrastructure.users;

import pl.tks.gr3.cinema.domain_model.users.Client;

public interface UpdateUserPort {

    void updateClient(Client client);
}
