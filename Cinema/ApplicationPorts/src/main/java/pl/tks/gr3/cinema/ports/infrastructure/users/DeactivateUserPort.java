package pl.tks.gr3.cinema.ports.infrastructure.users;

import pl.tks.gr3.cinema.domain_model.users.User;

public interface DeactivateUserPort {
    void deactivate(User user);
}
