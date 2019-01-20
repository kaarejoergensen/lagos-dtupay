package base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.rabbitmq.client.*;
import cucumber.runtime.Timeout;
import utils.JSONMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public abstract class RPCServer {
    public RPCServer() {
    }

    public void run(String host, String queueName) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        System.out.println(" [x] Connecting to host " + (host != null ? host : "localhost") );
        factory.setHost(host != null ? host : "localhost");
        factory.setUsername("rabbitmq");
        factory.setPassword("rabbitmq");
        boolean connectionSuccess = false;
        for (int i = 0; i < 6; i++) {
            try {
                Connection connectionTest = factory.newConnection();
                connectionTest.close();
                connectionSuccess = true;
                break;
            } catch (IOException e) {
                System.out.println("Server: Connection refused, waiting 5 seconds");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        if (!connectionSuccess)
            throw new TimeoutException("Connection to broker could not be established!");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(queueName, false, false, false, null);
            channel.queuePurge(queueName);

            channel.basicQos(1);

            System.out.println(" [x] Awaiting RPC requests");

            Object monitor = new Object();
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                        .Builder()
                        .correlationId(delivery.getProperties().getCorrelationId())
                        .build();

                String response = "";

                try {
                    String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                    System.out.println(" [x] Received message: " + message);
                    response += this.implementation(JSONMapper.JSONToArray(message));
                    System.out.println(" [x] Answering: " + response);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                } finally {
                    channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, response.getBytes(StandardCharsets.UTF_8));
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    // RabbitMq consumer worker thread notifies the RPC server owner thread
                    synchronized (monitor) {
                        monitor.notify();
                    }
                }
            };

            channel.basicConsume(queueName, false, deliverCallback, (consumerTag -> { }));
            // Wait and be prepared to consume the message from RPC client.
            while (true) {
                synchronized (monitor) {
                    try {
                        monitor.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    protected String convertToJson(Object object) {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        try {
            return ow.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }

    protected String error(String errorMessage) {
        return "{\"error\" : \"" + errorMessage + "\"}";
    }

    protected abstract String implementation(String... arguments);
}