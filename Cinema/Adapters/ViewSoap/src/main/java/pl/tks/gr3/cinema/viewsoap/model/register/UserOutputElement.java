package pl.tks.gr3.cinema.viewsoap.model.register;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.tks.gr3.cinema.viewsoap.consts.WebServiceConsts;

@Getter
@NoArgsConstructor @AllArgsConstructor
@XmlRootElement(name = WebServiceConsts.USER_OUTPUT_ELEMENT)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"id", "login", "statusActive"})
public class UserOutputElement {

    @XmlElement(required = true, name ="id")
    private String id;

    @XmlElement(required = true, name = "login")
    private String login;

    @XmlElement(required = true, name = "statusActive")
    private boolean statusActive;
}
