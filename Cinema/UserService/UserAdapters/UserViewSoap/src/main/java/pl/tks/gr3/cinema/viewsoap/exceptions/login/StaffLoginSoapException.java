package pl.tks.gr3.cinema.viewsoap.exceptions.login;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.CLIENT)
public class StaffLoginSoapException extends LoginSoapException {
    public StaffLoginSoapException(String message) {
        super(message);
    }
}
