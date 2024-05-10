package pl.tks.gr3.cinema.viewsoap.model.register;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.tks.gr3.cinema.viewsoap.consts.WebServiceConsts;

@Getter
@NoArgsConstructor @AllArgsConstructor
@XmlRootElement(name = WebServiceConsts.ADMIN_REGISTER_OUTPUT)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"admin", "accessToken"})
public class AdminRegisterResponse {

    @XmlElement(required = true, name = "admin")
    private UserOutputElement admin;

    @XmlElement(required = true, name = "accessToken")
    private String accessToken;
}
