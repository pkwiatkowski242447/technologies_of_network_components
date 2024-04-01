@XmlSchema(
        namespace = "http://viewsoap.adapters.cinema/users",
        elementFormDefault = XmlNsForm.QUALIFIED,
        attributeFormDefault = XmlNsForm.QUALIFIED,
        xmlns = {
                @XmlNs(prefix = "tickets", namespaceURI = "http://viewsoap.adapters.cinema/tickets"),
                @XmlNs(prefix = "users", namespaceURI = "http://viewsoap.adapters.cinema/users"),
                @XmlNs(prefix = "movies", namespaceURI = "http://viewsoap.adapters.cinema/movies")
        }
)

package pl.tks.gr3.cinema.viewsoap.model.users;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;