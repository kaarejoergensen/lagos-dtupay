package com.dtupay.dtupayapi;

import clients.BankClient;
import clients.TokenClient;
import exceptions.ClientException;
import models.User;
import org.junit.*;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.shaded.javax.ws.rs.client.Client;
import org.testcontainers.shaded.javax.ws.rs.client.ClientBuilder;
import org.testcontainers.shaded.javax.ws.rs.client.Entity;
import org.testcontainers.shaded.javax.ws.rs.client.WebTarget;
import org.testcontainers.shaded.javax.ws.rs.core.Form;
import org.testcontainers.shaded.javax.ws.rs.core.MediaType;
import org.testcontainers.shaded.javax.ws.rs.core.Response;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;


public class  CustomerTest {

    BankClient bank = new BankClient("localhost", "kode", "her");
    TokenClient tokenClient = new TokenClient("localhost", "kode", "her");

    private String customerUid;
    private String merchantUid;

    public CustomerTest() throws IOException, TimeoutException {
    }

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

    @Test
    public void testRequestTokens() {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://localhost:8080/v1/customer/requestTokens");

        Form form = new Form();
        form.param("name", "Lagos");
        form.param("uid", customerUid);
        form.param("count", "0");

        Response response = webTarget.request(MediaType.APPLICATION_JSON).post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void testBarcode() {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://localhost:8080/v1/customer/barcode/");

    }

    //Customer report
    @Test
    public void testGetCustomerTransactions() {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://localhost:8080/v1/customer/transactions");

        Form form = new Form();
        form.param("uid", customerUid);
        form.param("fromDate",  "07-01-2019");
        form.param("toDate","26-01-2019");

        Response response = webTarget.request(MediaType.APPLICATION_JSON).post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    //Merchant report
    @Test
    public void testGetMerchantTransactions() {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://localhost:8081/v1/merchant/transactions");

        Form form = new Form();
        form.param("uid", merchantUid);
        form.param("fromDate",  "07-01-2019");
        form.param("toDate","26-01-2019");

        Response response = webTarget.request(MediaType.APPLICATION_JSON).post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }


    @Test
    public void testTransfer() {
    Client client = ClientBuilder.newClient();
    WebTarget webTarget = client.target("http://localhost:8081/v1/merchant/");

    Form form = new Form();
    form.param("cuid", customerUid);
    form.param("muid", merchantUid);
    form.param("price", "100");
    form.param("description", "some money");

    Response response = webTarget.request(MediaType.APPLICATION_JSON).post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

    }



    @Before
    public void setup () {
        try {
            bank.retireAccount(bank.getAccountByCprNumber("1234123423").getId());
            bank.retireAccount(bank.getAccountByCprNumber("1234123433").getId());
            bank.createAccountWithBalance(new User("1234123423", "Lagos", "customer"), BigDecimal.valueOf(1000));
            bank.createAccountWithBalance(new User("1234123433", "Lagos", "merchant"), BigDecimal.valueOf(1000));
            this.customerUid = bank.getAccountByCprNumber("1234123423").getId();
            this.merchantUid = bank.getAccountByCprNumber("1234123433").getId();
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown () {
        try {
            bank.retireAccount(bank.getAccountByCprNumber("1234123423").getId());
            bank.retireAccount(bank.getAccountByCprNumber("1234123433").getId());
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }



}
