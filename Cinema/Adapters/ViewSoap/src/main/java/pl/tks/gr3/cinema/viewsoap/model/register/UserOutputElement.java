package pl.tks.gr3.cinema.viewsoap.model.register;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
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

    // Equals method

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        UserOutputElement that = (UserOutputElement) o;

        return new EqualsBuilder()
                .append(statusActive, that.statusActive)
                .append(id, that.id)
                .append(login, that.login)
                .isEquals();
    }

    // HashCode method

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(login)
                .append(statusActive)
                .toHashCode();
    }
}
