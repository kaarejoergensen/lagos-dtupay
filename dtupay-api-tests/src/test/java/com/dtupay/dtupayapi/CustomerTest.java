package com.dtupay.dtupayapi;

import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.shaded.javax.ws.rs.client.Client;
import org.testcontainers.shaded.javax.ws.rs.client.ClientBuilder;
import org.testcontainers.shaded.javax.ws.rs.client.WebTarget;

import java.io.File;

@Ignore
public class CustomerTest {

    @ClassRule
    public static DockerComposeContainer environment =
            new DockerComposeContainer(new File("../../../../docker-compose.yml"))
                    .withExposedService("dtupay-api-customer", 8080, Wait.forHttp("/all")
                            .forStatusCode(200).forStatusCode(401))
                    .withExposedService("dtupay-api-merchant", 8081, Wait.forHttp("/all")
                            .forStatusCode(200).forStatusCode(401))
                    .withExposedService("dtupay-api-manager", 8082, Wait.forHttp("/all")
                            .forStatusCode(200).forStatusCode(401));

    @Test
    public void test() {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://localhost:8080/v1/customer");
        String result = webTarget.path("").request().get(String.class);
        System.out.println(result);
    }
}
