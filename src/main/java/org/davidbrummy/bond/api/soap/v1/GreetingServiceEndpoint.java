package org.davidbrummy.bond.api.soap.v1;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.springframework.stereotype.Service;

@Service("greetingServiceEndpoint")
@WebService(serviceName = "GreetingService")
public class GreetingServiceEndpoint {

    @WebMethod
    public String sayHello() {
        return "hello world";
    }

}
