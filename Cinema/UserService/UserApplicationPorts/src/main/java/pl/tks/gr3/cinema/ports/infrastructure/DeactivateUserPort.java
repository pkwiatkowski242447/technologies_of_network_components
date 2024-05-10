package pl.tks.gr3.cinema.ports.infrastructure;

import pl.tks.gr3.cinema.domain_model.User;

public interface DeactivateUserPort {

    void deactivate(User user);
}
