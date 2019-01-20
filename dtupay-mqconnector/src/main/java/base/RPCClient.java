package base;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import exceptions.ClientException;
import utils.JSONMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

public class RPCClient implements AutoCloseable {
    private Connection connection;
    private Channel channel;
    private String queueName;

    public RPCClient(String host, String queueName) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host != null ? host : "localhost");
        factory.setUsername("rabbitmq");
        factory.setPassword("rabbitmq");

        connection = factory.newConnection();
        channel = connection.createChannel();
        this.queueName = queueName;
    }

    public String call(String... arguments) throws IOException, InterruptedException, ClientException {
        System.out.println(" [ ] call: " + Arrays.toString(arguments));
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
                System.out.println("Delivery: " + new String(delivery.getBody(), StandardCharsets.UTF_8));
                response.offer(new String(delivery.getBody(), StandardCharsets.UTF_8));
            }
        }, consumerTag -> {
        });

        String result = response.take();
        channel.basicCancel(ctag);
        System.out.println(" [ ] received: " + result);
        String error = JSONMapper.JSONToExceptionMessage(result);
        if (error != null)
            throw new ClientException(error);
        return result;
    }

    public void close() throws IOException {
        connection.close();
    }
}
