package com.dtupay.dtupayapi;

import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.containers.wait.strategy.Wait;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.io.File;
import java.time.Duration;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class CustomerIT {
    private static final String COMPOSE_FILE_LOCATION = System.getProperty("user.dir") + "/src/test/resources/compose-test.yml";

    private static final String CUSTOMER_SERVICE = "api-customer_1";
    private static final String MERCHANT_SERVICE = "api-merchant_1";
    private static final String MANAGER_SERVICE = "api-manager_1";

    private static final int CUSTOMER_PORT = 8080;
    private static final int MERCHANT_PORT = 8081;
    private static final int MANAGER_PORT = 8082;

    private static final String LOG_REGEX = ".*Thorntail is Ready.*";

    @ClassRule
    public static DockerComposeContainer environment =
            new DockerComposeContainer(new File(COMPOSE_FILE_LOCATION))
                    .withExposedService(CUSTOMER_SERVICE, CUSTOMER_PORT,
                            //Wait.forHttp("/").forStatusCode(404))
                            Wait.forLogMessage(LOG_REGEX, 1).withStartupTimeout(Duration.ofMinutes(2)))
                    .withExposedService(MERCHANT_SERVICE, MERCHANT_PORT,
                            //Wait.forHttp("/").forStatusCode(404))
                            Wait.forLogMessage(LOG_REGEX, 1).withStartupTimeout(Duration.ofMinutes(2)))
                    .withExposedService(MANAGER_SERVICE, MANAGER_PORT,
                            //Wait.forHttp("/").forStatusCode(404));
                            Wait.forLogMessage(LOG_REGEX, 1).withStartupTimeout(Duration.ofMinutes(2)))
                    .withLogConsumer(CUSTOMER_SERVICE, outputFrame -> {
                        if (((OutputFrame)outputFrame).getUtf8String().contains("Thorntail is Ready")) {
                            System.out.println(CUSTOMER_SERVICE + " is ready!");
                            System.out.println(((OutputFrame)outputFrame).getUtf8String());
                        }
                    })
                    .withLogConsumer(MERCHANT_SERVICE, outputFrame -> {
                        if (((OutputFrame)outputFrame).getUtf8String().contains("Thorntail is Ready")) {
                            System.out.println(MERCHANT_SERVICE + " is ready!");
                            System.out.println(((OutputFrame)outputFrame).getUtf8String());
                        }
                    })
                    .withLogConsumer(MANAGER_SERVICE, outputFrame -> {
                        if (((OutputFrame)outputFrame).getUtf8String().contains("Thorntail is Ready")) {
                            System.out.println(MANAGER_SERVICE + " is ready!");
                            System.out.println(((OutputFrame)outputFrame).getUtf8String());
                        }
                    });
                    //.withLogConsumer(CUSTOMER_SERVICE, new Slf4jLogConsumer(LoggerFactory.getLogger(CustomerIT.class)))
                    //.withLogConsumer(MERCHANT_SERVICE, new Slf4jLogConsumer(LoggerFactory.getLogger(CustomerIT.class)))
                    //.withLogConsumer(MANAGER_SERVICE, new Slf4jLogConsumer(LoggerFactory.getLogger(CustomerIT.class)));

    @Test
    public void test() {
        Client client = ClientBuilder.newClient();
        String url = "http://" +
                environment.getServiceHost(CUSTOMER_SERVICE, CUSTOMER_PORT) +
                ":" +
                environment.getServicePort(CUSTOMER_SERVICE, CUSTOMER_PORT) +
                "/v1/customer";
        System.out.println(url);
        WebTarget webTarget = client.target(url);
        String result = webTarget.request().get(String.class);
        System.out.println(result);
        assertThat(result, is("You did it!!!!"));
    }
}
