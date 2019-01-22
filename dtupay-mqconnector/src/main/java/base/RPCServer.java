package base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.rabbitmq.client.*;
import utils.JSONMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public abstract class RPCServer {
    public RPCServer() {
    }

    public void run(String host, String queueName) throws IOException, TimeoutException {
        ConnectionFactory defaultConnectionFactory = new ConnectionFactory();
        this.run(host, queueName, defaultConnectionFactory.getUsername(), defaultConnectionFactory.getPassword());
    }

    public void run(String host, String queueName, String username, String password) throws IOException, TimeoutException {
        this.run(host, queueName, username, password, -1);
    }

    public void run(String host, String queueName, String username, String password, int port) throws IOException, TimeoutException {
        if (host == null || queueName == null)
            throw new IllegalArgumentException("No arguments can be null or empty");

        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setHost(host);
        factory.setPort(port);
        boolean connectionSuccess = false;
        for (int numberOfTries = 12; numberOfTries > 0; numberOfTries--) {
            try {
                factory.newConnection();
                connectionSuccess = true;
                System.out.println("Connection to " + host + " succeeded!");
                break;
            } catch (IOException e) {
                System.err.println("Connection to " + host + " could not be established sleeping 5 seconds");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        if (!connectionSuccess)
            throw new TimeoutException("Connection to broker could not be established");

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
                    String[] arguments = JSONMapper.JSONToArray(message);
                    System.out.println(" [x] Received call: " + arguments[0]);
                    response += this.implementation(arguments);
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