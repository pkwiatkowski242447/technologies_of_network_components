package pl.tks.gr3.cinema.viewsoap.model.register;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.tks.gr3.cinema.utils.consts.UserConstants;
import pl.tks.gr3.cinema.utils.validation.UserValidationMessages;
import pl.tks.gr3.cinema.viewsoap.consts.WebServiceConsts;

@Getter
@NoArgsConstructor @AllArgsConstructor
@XmlRootElement(name = WebServiceConsts.STAFF_REGISTER_INPUT)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"login", "password"})
public class StaffRegisterRequest {

    @XmlElement(required = true, name = "login")
    @NotBlank(message = UserValidationMessages.NULL_LOGIN)
    @Size(min = UserConstants.LOGIN_MIN_LENGTH, message = UserValidationMessages.LOGIN_TOO_SHORT)
    @Size(max = UserConstants.LOGIN_MAX_LENGTH, message = UserValidationMessages.LOGIN_TOO_LONG)
    private String login;

    @XmlElement(required = true, name = "password")
    @NotBlank(message = UserValidationMessages.NULL_PASSWORD)
    @Size(min = UserConstants.PASSWORD_MIN_LENGTH, message = UserValidationMessages.PASSWORD_TOO_SHORT)
    @Size(max = UserConstants.PASSWORD_MAX_LENGTH, message = UserValidationMessages.PASSWORD_TOO_LONG)
    private String password;
}
