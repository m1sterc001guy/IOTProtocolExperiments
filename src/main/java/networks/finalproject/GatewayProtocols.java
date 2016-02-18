package networks.finalproject;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class GatewayProtocols {

  private final static String EXCHANGE_NAME = "topic_logs";

  public static void main( String[] args ) throws IOException , TimeoutException {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setUsername("test");
    factory.setPassword("test");
    factory.setHost("192.168.1.152");
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();

    channel.exchangeDeclare(EXCHANGE_NAME, "topic");

    String routingKey = getRouting(args);
    String message = getMessage(args);

    channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes("UTF-8"));
    System.out.println(" [x] Sent '" + routingKey + "':'" + message + "'");

    connection.close();
  }

  private static String getRouting(String[] strings) {
    if (strings.length < 1) return "anonymous.info";
    return strings[0];
  }

  private static String getMessage(String[] strings) {
    if (strings.length < 2) return "Hello World!";
    return joinStrings(strings, " ", 1);
  }

  private static String joinStrings(String[] strings, String delimiter, int startIndex) {
    int length = strings.length;
    if (length == 0) return "";
    if (length < startIndex) return "";
    StringBuilder words = new StringBuilder(strings[startIndex]);
    for (int i = startIndex + 1; i < length; i++) {
      words.append(delimiter).append(strings[i]);
    }
    return words.toString();
  }
}
