package pl.tks.gr3.cinema.viewsoap.model.login;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.tks.gr3.cinema.viewsoap.consts.WebServiceConsts;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = WebServiceConsts.STAFF_LOGIN_INPUT)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"login", "password"})
public class StaffLoginRequest {

    @XmlElement(required = true, name = "login")
    private String login;

    @XmlElement(required = true, name = "password")
    private String password;
}
