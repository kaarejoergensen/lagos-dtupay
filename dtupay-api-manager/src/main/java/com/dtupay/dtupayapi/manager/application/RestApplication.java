package com.dtupay.dtupayapi.manager.application;

import com.dtupay.dtupayapi.manager.endpoints.ManagerEndpoint;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeoutException;

@ApplicationPath("/")
public class RestApplication extends Application {

    @Override
    public Set<Object> getSingletons() {
        String rabbitHost = System.getProperty("rabbit.host");
        String rabbitUsername = System.getProperty("rabbit.username");
        String rabbitPassword = System.getProperty("rabbit.password");

        ManagerEndpoint managerEndpoint = new ManagerEndpoint();
        try {
            managerEndpoint.setRabbitMQInfo(rabbitHost, rabbitUsername, rabbitPassword);
        } catch (IOException | TimeoutException e) {
            System.err.println("Failed creating customerEndpoint");
            return Collections.emptySet();
        }
        Set<Object> set = new HashSet<>();
        set.add(managerEndpoint);
        return set;
    }
}
