package pl.tks.gr3.cinema.viewsoap.model.register;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.tks.gr3.cinema.viewsoap.consts.WebServiceConsts;

@Getter
@NoArgsConstructor @AllArgsConstructor
@XmlRootElement(name = WebServiceConsts.CLIENT_REGISTER_OUTPUT)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"client", "accessToken"})
public class ClientRegisterResponse {

    @XmlElement(required = true, name = "client")
    private UserOutputElement client;

    @XmlElement(required = true, name = "accessToken")
    private String accessToken;
}
