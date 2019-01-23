package com.dtupay.dtupayapi;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.time.Duration;

import static org.hamcrest.MatcherAssert.assertThat;

@Ignore
public class CustomerIT {
    private static final String COMPOSE_FILE_LOCATION = "/src/test/resources/compose-test.yml";

    private static final String RABBIT_LOG_REGEX = ".*Server startup complete.*";
    private static final String MONGO_LOG_REGEX = ".*build index done.*";
    private static final String REST_LOG_REGEX = ".*Thorntail is Ready.*";

    private static String customerURL;
    private static String merchantURL;
    private static String managerURL;

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
        customerURL = "http://" + customer.getContainerIpAddress() + ":" + customer.getFirstMappedPort() + "/v1/customer";
        merchantURL = "http://" + merchant.getContainerIpAddress() + ":" + merchant.getFirstMappedPort() + "/v1/merchant";
        managerURL = "http://" + manager.getContainerIpAddress() + ":" + manager.getFirstMappedPort() + "/v1/manager";
    }

    @Test
    public void test() {
        Client client = ClientBuilder.newClient();
        System.out.println(customerURL);
        WebTarget webTarget = client.target(customerURL);
        String result = webTarget.request().get(String.class);
        System.out.println(result);
        assertThat(result, is("You did it!"));
    }
}
