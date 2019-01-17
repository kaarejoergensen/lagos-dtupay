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

public class RPCClient implements AutoCloseable {

    private Connection connection;
    private Channel channel;

    public RPCClient() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("rabbitmq");
        factory.setPassword("rabbitmq");

        connection = factory.newConnection();
        channel = connection.createChannel();
    }

    //public static void main(String[] argv) {
     //   try (base.RPCClient fibonacciRpc = new base.RPCClient()) {
      //      List<String> arguments = Arrays.asList("core", "1234", "5");
       //     System.out.println(fibonacciRpc.call(arrayToJSON(arguments)));
        //} catch (IOException | TimeoutException | InterruptedException e) {
         //   e.printStackTrace();
        //}
    //}

    public String call(String... arguments) throws IOException, InterruptedException, ClientException {
        final String corrId = UUID.randomUUID().toString();
        final String message = JSONMapper.arrayToJSON(arguments);

        String replyQueueName = channel.queueDeclare().getQueue();
        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(corrId)
                .replyTo(replyQueueName)
                .build();

        channel.basicPublish("", RPCServer.RPC_QUEUE_NAME, props, message.getBytes(StandardCharsets.UTF_8));

        final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);

        String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
            if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                response.offer(new String(delivery.getBody(), StandardCharsets.UTF_8));
            }
        }, consumerTag -> {
        });

        String result = response.take();
        channel.basicCancel(ctag);
        String error = JSONMapper.JSONToExceptionMessage(result);
        if (error != null)
            throw new ClientException(error);
        return result;
    }

    public void close() throws IOException {
        connection.close();
    }
}
