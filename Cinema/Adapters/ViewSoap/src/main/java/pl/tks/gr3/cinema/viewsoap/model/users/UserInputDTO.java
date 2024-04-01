package pl.tks.gr3.cinema.viewsoap.model.users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "login",
        "password"
})
@XmlRootElement(name = "userInputDTO")
public class UserInputDTO {

    @XmlElement(required = true, name = "userLogin")
    private String login;

    @XmlElement(required = true, name = "userPassword")
    private String password;
}

