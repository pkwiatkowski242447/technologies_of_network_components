package pl.tks.gr3.cinema.viewsoap.exceptions.login;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.CLIENT)
public class ClientLoginSoapException extends LoginSoapException {
    public ClientLoginSoapException(String message) {
        super(message);
    }
}
