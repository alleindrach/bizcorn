package allein.servicefeign.service;

import org.springframework.stereotype.Component;

@Component
public class ServiceGateHystric implements  ServiceGate {
    @Override
    public String sayHiFromClientOne(String name) {
        return "sorry "+name;
    }
}
