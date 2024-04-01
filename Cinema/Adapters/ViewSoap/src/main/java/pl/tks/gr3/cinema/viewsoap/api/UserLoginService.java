package pl.tks.gr3.cinema.viewsoap.api;

import pl.tks.gr3.cinema.viewsoap.model.users.UserInputDTO;


public interface UserLoginService {

    String loginClient(UserInputDTO userInput);
    String loginStaff(UserInputDTO staffInput);
    String loginAdmin(UserInputDTO adminInput);
}
