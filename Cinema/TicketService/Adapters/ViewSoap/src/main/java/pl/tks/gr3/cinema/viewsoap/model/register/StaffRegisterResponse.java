package pl.tks.gr3.cinema.viewsoap.model.register;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.tks.gr3.cinema.viewsoap.consts.WebServiceConsts;

@Getter
@NoArgsConstructor @AllArgsConstructor
@XmlRootElement(name = WebServiceConsts.STAFF_REGISTER_OUTPUT)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"staff", "accessToken"})
public class StaffRegisterResponse {

    @XmlElement(required = true, name = "staff")
    private UserOutputElement staff;

    @XmlElement(required = true, name = "accessToken")
    private String accessToken;
}
