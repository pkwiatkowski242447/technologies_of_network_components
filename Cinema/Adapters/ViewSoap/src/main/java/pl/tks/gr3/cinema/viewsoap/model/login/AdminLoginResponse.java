package pl.tks.gr3.cinema.viewsoap.model.login;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.tks.gr3.cinema.viewsoap.consts.WebServiceConsts;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = WebServiceConsts.ADMIN_LOGIN_OUTPUT)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"accessToken"})
public class AdminLoginResponse {

    @XmlElement(required = true, name ="accessToken")
    String accessToken;
}
