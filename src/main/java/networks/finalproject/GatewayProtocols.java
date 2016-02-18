package networks.finalproject;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class GatewayProtocols {

  private final static String QUEUE_NAME = "hello";

  public static void main( String[] args ) throws IOException , TimeoutException {
    System.out.println("Creating Factory");
    ConnectionFactory factory = new ConnectionFactory();
    factory.setUsername("test");
    factory.setPassword("test");
    factory.setHost("192.168.1.152");
    System.out.println("Creating Connection");
    Connection connection = factory.newConnection();
    System.out.println("Creating channel");
    Channel channel = connection.createChannel();

    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
    String message = "Hello, World";
    System.out.println("Publishing Message");
    channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
    System.out.println(" [x] Sent '" + message + "'");

    channel.close();
    connection.close();
  }
}
