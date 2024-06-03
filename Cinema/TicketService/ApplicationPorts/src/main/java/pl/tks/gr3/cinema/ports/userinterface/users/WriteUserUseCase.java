package pl.tks.gr3.cinema.ports.userinterface.users;

import java.util.UUID;

public interface WriteUserUseCase<Type> {

    Type create(UUID uuid, String login);
    void update(Type element);
    void delete(UUID userID);
    void activate(UUID elementID);
    void deactivate(UUID elementID);
}
