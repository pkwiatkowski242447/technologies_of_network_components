@XmlSchema(
        namespace = "http://viewsoap.adapters.cinema/movies",
        elementFormDefault = XmlNsForm.QUALIFIED,
        attributeFormDefault = XmlNsForm.QUALIFIED,
        xmlns = {
                @XmlNs(prefix = "movies", namespaceURI = "http://viewsoap.adapters.cinema/movies"),
                @XmlNs(prefix = "users", namespaceURI = "http://viewsoap.adapters.cinema/users"),
                @XmlNs(prefix = "tickets", namespaceURI = "http://viewsoap.adapters.cinema/tickets")
        }
)

package pl.tks.gr3.cinema.viewsoap.model.movies;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;