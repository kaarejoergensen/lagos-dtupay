package com.dtupay.dtupayapi;

import com.sun.security.ntlm.Client;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import java.io.File;

import static junit.framework.TestCase.assertEquals;

/**
@auther Fredrik
 */

@Ignore
public class ManagerTest {

    private String managerId;

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
    public void testCreateAccount() {
        javax.ws.rs.client.Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://localhost:8082/v1/merchant/transactions");

        String expected = "";

        Form form = new Form();
        form.param("uid", "1234");
        form.param("firstName","fredrik");
        form.param("lastName","kloster");

        webTarget.request(MediaType.APPLICATION_JSON).post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        assertEquals(expected, webTarget.request().get(String.class));
    }


}
