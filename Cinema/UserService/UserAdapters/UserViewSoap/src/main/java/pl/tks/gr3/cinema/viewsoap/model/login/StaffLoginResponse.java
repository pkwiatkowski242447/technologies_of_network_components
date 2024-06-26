package pl.tks.gr3.cinema.viewsoap.model.login;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.tks.gr3.cinema.viewsoap.consts.WebServiceConsts;

@Getter
@NoArgsConstructor @AllArgsConstructor
@XmlRootElement(name = WebServiceConsts.STAFF_LOGIN_OUTPUT)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"accessToken"})
public class StaffLoginResponse {

    @XmlElement(required = true, name ="accessToken")
    String accessToken;
}
