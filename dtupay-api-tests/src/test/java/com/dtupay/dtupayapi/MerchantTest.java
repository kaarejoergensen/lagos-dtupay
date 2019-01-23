package com.dtupay.dtupayapi;

import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;
/**
    @author Fredrik
 */
@Ignore
public class MerchantTest {

    private String merchantUid;

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
    public void testGetMerchantTransaction() {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://localhost:8081/v1/merchant/transactions");

        String expected = "";

        Form form = new Form();
        form.param("uid", merchantUid);
        form.param("fromDate",  "07-01-2019");
        form.param("toDate","26-01-2019");

        webTarget.request(MediaType.APPLICATION_JSON).post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        assertEquals(expected, webTarget.request().get(String.class));
    }


}
