package base;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import exceptions.ClientException;
import utils.JSONMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

public abstract class RPCClient implements AutoCloseable {
    private Connection connection;
    private Channel channel;
    private String queueName;

    public RPCClient(String host, String queueName) throws IOException, TimeoutException {
        this(host, queueName, new ConnectionFactory().getUsername(), new ConnectionFactory().getPassword());
    }

    public RPCClient(String host, String queueName, String username, String password) throws IOException, TimeoutException {
        this(host, queueName, username, password, -1);
    }

    public RPCClient(String host, String queueName, String username, String password, int port) throws IOException, TimeoutException {
        if (host == null || queueName == null)
            throw new IllegalArgumentException("No arguments can be null or empty");
        this.queueName = queueName;

        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setHost(host);
        factory.setPort(port);
        boolean connectionSuccess = false;
        for (int numberOfTries = 12; numberOfTries > 0; numberOfTries--) {
            try {
                connection = factory.newConnection();
                connectionSuccess = true;
                System.out.println("Connection to " + host + " succeeded!");
                break;
            } catch (IOException e) {
                System.err.println("Connection to " + host + " could not be established, sleeping for 5 seconds");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        if (!connectionSuccess)
            throw new TimeoutException("Connection to broker could not be established");

        channel = connection.createChannel();
    }

    public String call(String... arguments) throws IOException, InterruptedException, ClientException {
        System.out.println(" [ ] call: " + arguments[0]);
        final String corrId = UUID.randomUUID().toString();
        final String message = JSONMapper.arrayToJSON(arguments);

        String replyQueueName = channel.queueDeclare().getQueue();
        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(corrId)
                .replyTo(replyQueueName)
                .build();

        channel.basicPublish("", queueName, props, message.getBytes(StandardCharsets.UTF_8));

        final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);

        String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
            if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                response.offer(new String(delivery.getBody(), StandardCharsets.UTF_8));
            }
        }, consumerTag -> {
        });

        String result = response.take();
        channel.basicCancel(ctag);
        System.out.println(" [ ] received response");
        String error = JSONMapper.JSONToExceptionMessage(result);
        if (error != null)
            throw new ClientException(error);
        return result;
    }

    public void close() throws IOException {
        connection.close();
    }
}
