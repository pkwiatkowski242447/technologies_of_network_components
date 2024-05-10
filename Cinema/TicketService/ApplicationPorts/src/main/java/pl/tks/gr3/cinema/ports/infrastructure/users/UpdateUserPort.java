package pl.tks.gr3.cinema.ports.infrastructure.users;

import pl.tks.gr3.cinema.domain_model.users.Admin;
import pl.tks.gr3.cinema.domain_model.users.Client;
import pl.tks.gr3.cinema.domain_model.users.Staff;

public interface UpdateUserPort {

    void updateClient(Client client);
    void updateAdmin(Admin admin);
    void updateStaff(Staff staff);
}
