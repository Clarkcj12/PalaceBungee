package network.palace.bungee.messages;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import lombok.Getter;
import network.palace.bungee.PalaceBungee;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Getter
public class MessageClient {
    private Channel channel;
    private final String name;
    private boolean queue;

    public MessageClient(ConnectionType type, String exchange, String exchangeType) throws Exception {
        queue = false;
        this.channel = PalaceBungee.getMessageHandler().getConnection(type).createChannel();
        this.name = exchange;
        channel.exchangeDeclare(exchange, exchangeType);
        channel.addShutdownListener(e -> {
            PalaceBungee.getProxyServer().getLogger().warning("The " + exchange + " channel has been closed - recreating!");
            try {
                channel = PalaceBungee.getMessageHandler().getConnection(type).createChannel();
            } catch (IOException | TimeoutException ioException) {
                ioException.printStackTrace();
                PalaceBungee.getProxyServer().getLogger().warning("There was an error recreating the " + exchange + " channel!");
            }
        });
    }

    public MessageClient(ConnectionType type, String queueName, boolean durable) throws Exception {
        queue = true;
        this.channel = PalaceBungee.getMessageHandler().getConnection(type).createChannel();
        this.name = queueName;
        // queueName, durable, exclusive, autoDelete, args
        channel.queueDeclare(queueName, durable, false, false, null);
    }

    public void basicPublish(byte[] bytes) throws IOException {
        basicPublish(bytes, "");
    }

    public void basicPublish(byte[] bytes, String routingKey) throws IOException {
        basicPublish(bytes, routingKey, MessageHandler.JSON_PROPS);
    }

    public void basicPublish(byte[] bytes, String routingKey, AMQP.BasicProperties props) throws IOException {
        if (queue) {
            channel.basicPublish(routingKey, name, props, bytes);
        } else {
            channel.basicPublish(name, routingKey, props, bytes);
        }
    }
}
