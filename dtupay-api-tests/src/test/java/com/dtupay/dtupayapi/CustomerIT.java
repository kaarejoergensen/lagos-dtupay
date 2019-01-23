package com.dtupay.dtupayapi;


import com.dtupay.dtupayapi.customer.models.TokenBarcodePathPair;
import gherkin.deps.com.google.gson.Gson;
import gherkin.deps.com.google.gson.reflect.TypeToken;
import models.Transaction;
import org.junit.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertEquals;

/**
 * @author Kåre
 */
public class CustomerIT {
    private static final int REST_PORT = 8080;

    private static final String RABBIT_LOG_REGEX = ".*Server startup complete.*";
    private static final String MONGO_LOG_REGEX = ".*build index done.*";
    private static final String REST_LOG_REGEX = ".*Thorntail is Ready.*";

    private static String customerURL;
    private static String merchantURL;
    private static String managerURL;

    private static String customerUid;
    private static String merchantUid;

    @ClassRule
    public static Network network = Network.newNetwork();

    @ClassRule
    public static GenericContainer rabbitmq = new GenericContainer<>("rabbitmq")
            .withEnv("RABBITMQ_DEFAULT_USER", "rabbitmq").withEnv("RABBITMQ_DEFAULT_PASS", "rabbitmq")
            .waitingFor(Wait.forLogMessage(RABBIT_LOG_REGEX, 1))
            .withNetwork(network).withNetworkAliases("rabbitmq");

    @ClassRule
    public static GenericContainer mongo = new GenericContainer<>("mongo")
            .waitingFor(Wait.forLogMessage(MONGO_LOG_REGEX, 1))
            .withNetwork(network).withNetworkAliases("mongo");

    @ClassRule
    public static GenericContainer bank = new GenericContainer<>("lagos/dtupay-bank:local")
            .withEnv("BROKER_HOST_USERNAME", "rabbitmq").withEnv("BROKER_HOST_PASSWORD", "rabbitmq")
            .withEnv("BROKER_HOST_NAME", "rabbitmq").withNetwork(network);

    @ClassRule
    public static GenericContainer token = new GenericContainer<>("lagos/dtupay-token:local")
            .withEnv("BROKER_HOST_USERNAME", "rabbitmq").withEnv("BROKER_HOST_PASSWORD", "rabbitmq")
            .withEnv("BROKER_HOST_NAME", "rabbitmq").withEnv("MONGO_HOST_NAME", "mongo").withNetwork(network);

    @ClassRule
    public static GenericContainer customer = new GenericContainer<>("lagos/dtupay-api-customer:local")
            .withEnv("BROKER_HOST_USERNAME", "rabbitmq").withEnv("BROKER_HOST_PASSWORD", "rabbitmq")
            .withEnv("BROKER_HOST_NAME", "rabbitmq").withExposedPorts(REST_PORT).withNetwork(network)
            .waitingFor(Wait.forLogMessage(REST_LOG_REGEX, 1).withStartupTimeout(Duration.ofMinutes(4)));

    @ClassRule
    public static GenericContainer merchant = new GenericContainer<>("lagos/dtupay-api-merchant:local")
            .withEnv("BROKER_HOST_USERNAME", "rabbitmq").withEnv("BROKER_HOST_PASSWORD", "rabbitmq")
            .withEnv("BROKER_HOST_NAME", "rabbitmq").withExposedPorts(REST_PORT).withNetwork(network)
            .waitingFor(Wait.forLogMessage(REST_LOG_REGEX, 1).withStartupTimeout(Duration.ofMinutes(4)));

    @ClassRule
    public static GenericContainer manager = new GenericContainer<>("lagos/dtupay-api-manager:local")
            .withEnv("BROKER_HOST_USERNAME", "rabbitmq").withEnv("BROKER_HOST_PASSWORD", "rabbitmq")
            .withEnv("BROKER_HOST_NAME", "rabbitmq").withExposedPorts(REST_PORT).withNetwork(network)
            .waitingFor(Wait.forLogMessage(REST_LOG_REGEX, 1).withStartupTimeout(Duration.ofMinutes(4)));

    @BeforeClass
    public static void init() {
        System.out.println("Running init");
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        customerURL = "http://" + customer.getContainerIpAddress() + ":" + customer.getFirstMappedPort() + "/v1/customer";
        merchantURL = "http://" + merchant.getContainerIpAddress() + ":" + merchant.getFirstMappedPort() + "/v1/merchant";
        managerURL = "http://" + manager.getContainerIpAddress() + ":" + manager.getFirstMappedPort() + "/v1/manager";

        Client client = ClientBuilder.newClient();
        WebTarget webTarget;
        System.out.println("Creating customer and merchant");
        webTarget = client.target(customerURL).path("createUser");

        System.out.println(webTarget.getUri());
        customerUid = webTarget
                .queryParam("username", "LagosCustomer")
                .queryParam("cprNumber", "1243224389")
                .queryParam("firstName", "LagosCustomer")
                .queryParam("lastName", "DTUpay")
                .request().post(Entity.entity("", MediaType.APPLICATION_JSON_TYPE), String.class);

        merchantUid = webTarget
                .queryParam("username", "LagosMerchant")
                .queryParam("cprNumber", "1234556456")
                .queryParam("firstName", "LagosMerchant")
                .queryParam("lastName", "DTUpay")
                .request().post(Entity.entity("", MediaType.APPLICATION_JSON_TYPE), String.class);
        System.out.println("Finished asfterClass");
    }

    @Test
    public void test() {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(customerURL);
        String result = webTarget.path("").request().get(String.class);
        System.out.println(result);
    }

    @Test
    public void testRequestTokens() {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(customerURL).path("requestTokens");

        Response response = webTarget
                .queryParam("name", "Lagos")
                .queryParam("uid", customerUid)
                .queryParam("count", 1)
                .request(MediaType.APPLICATION_JSON).post(Entity.entity("", MediaType.APPLICATION_FORM_URLENCODED_TYPE));

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void testBarcode() {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(customerURL).path("barcode/");

    }

    //Customer report
    @Test
    public void testGetCustomerTransactions() {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(customerURL).path("transactions");

        String response = webTarget
                .queryParam("userId", customerUid)
                .queryParam("from", "07-01-2019")
                .queryParam("to", "26-01-2019")
                .request(MediaType.APPLICATION_JSON_TYPE).get(String.class);
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Transaction>>(){}.getType();
        List<Transaction> transactionList = gson.fromJson(response, listType);
        assertThat(transactionList, is(notNullValue()));
    }

    //Merchant report
    @Test
    public void testGetMerchantTransactions() {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(merchantURL).path("transactions");

        String response = webTarget
                .queryParam("merchId", merchantUid)
                .queryParam("from", "07-01-2019")
                .queryParam("to", "26-01-2019")
                .request(MediaType.APPLICATION_JSON_TYPE).get(String.class);
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Transaction>>(){}.getType();
        List<Transaction> transactionList = gson.fromJson(response, listType);
        assertThat(transactionList, is(notNullValue()));
    }


    @Ignore
    @Test
    public void testTransfer() {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(customerURL).path("requestTokens");

        String response = webTarget
                .queryParam("name", "Lagos")
                .queryParam("uid", customerUid)
                .queryParam("count", 1)
                .request(MediaType.APPLICATION_JSON).post(Entity.entity("", MediaType.APPLICATION_FORM_URLENCODED_TYPE), String.class);
        Gson gson = new Gson();
        Type listType = new TypeToken<List<TokenBarcodePathPair>>(){}.getType();
        List<TokenBarcodePathPair> tokens = gson.fromJson(response, listType);
        assertThat(tokens.size(), is(1));
        System.out.println(tokens.get(0).getToken());

        WebTarget webTarget2 = client.target(merchantURL).path("payment");

        Response response2 = webTarget2
                .queryParam("token", tokens.get(0).getToken())
                .queryParam("merchId", merchantUid)
                .queryParam("price", new BigDecimal(100))
                .queryParam("description", "some money")
                .request(MediaType.APPLICATION_JSON).post(Entity.entity("", MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        assertEquals(Response.Status.OK.getStatusCode(), response2.getStatus());

    }

    @AfterClass
    public static void tearDown () {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(managerURL).path("user/retireAccount");
        String result = webTarget.queryParam("accountId", customerUid)
                .request().post(Entity.entity("", MediaType.APPLICATION_JSON_TYPE), String.class);
        result = webTarget.queryParam("accountId", merchantUid)
                .request().post(Entity.entity("", MediaType.APPLICATION_JSON_TYPE), String.class);
    }

}
