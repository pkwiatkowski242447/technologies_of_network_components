package pl.tks.gr3.cinema.ports.infrastructure;

import pl.tks.gr3.cinema.domain_model.User;

public interface ActivateUserPort {

    void activate(User user);
}
