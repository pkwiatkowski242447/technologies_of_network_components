package pl.tks.gr3.cinema.ports.infrastructure;

import pl.tks.gr3.cinema.domain_model.Admin;
import pl.tks.gr3.cinema.domain_model.Client;
import pl.tks.gr3.cinema.domain_model.Staff;

public interface UpdateUserPort {

    void updateClient(Client client);
    void updateAdmin(Admin admin);
    void updateStaff(Staff staff);
}
