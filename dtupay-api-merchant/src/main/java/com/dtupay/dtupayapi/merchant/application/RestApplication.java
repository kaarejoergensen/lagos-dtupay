package com.dtupay.dtupayapi.merchant.application;

import com.dtupay.dtupayapi.merchant.endpoints.MerchantEndpoint;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeoutException;

/**
 @author Kåre
 */

@ApplicationPath("/")
public class RestApplication extends Application {

    @Override
    public Set<Object> getSingletons() {
        String rabbitHost = System.getProperty("rabbit.host");
        String rabbitUsername = System.getProperty("rabbit.username");
        String rabbitPassword = System.getProperty("rabbit.password");

        MerchantEndpoint merchantEndpoint = new MerchantEndpoint();
        try {
            merchantEndpoint.setRabbitMQInfo(rabbitHost, rabbitUsername, rabbitPassword);
        } catch (IOException | TimeoutException e) {
            System.err.println("Failed creating merchantEndpoint");
            return Collections.emptySet();
        }
        Set<Object> set = new HashSet<>();
        set.add(merchantEndpoint);
        return set;
    }

}
