package pl.tks.gr3.cinema.viewsoap.exceptions.register;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.CLIENT)
public class DuplicateSoapException extends RegisterSoapException {
    public DuplicateSoapException(String message) {
        super(message);
    }
}
