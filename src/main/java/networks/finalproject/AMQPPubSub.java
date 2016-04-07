package networks.finalproject;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

public class AMQPPubSub implements IPubSub {

  static Logger log = Logger.getLogger(AMQPPubSub.class.getName());

  private ConnectionFactory factory;
  private Connection connection;
  private Channel channel;
  private String exchangeName;
  private long dataReceived;

  public AMQPPubSub(String username, String password, String brokerHost, String exchangeName) {
    this.exchangeName = exchangeName;
    factory = new ConnectionFactory();
    factory.setUsername(username);
    factory.setPassword(password);
    factory.setHost(brokerHost);
    dataReceived = 0;
  }

  @Override
  public boolean connectToBroker() {
    try {
      connection = factory.newConnection();
      channel = connection.createChannel();
      channel.exchangeDeclare(exchangeName, "topic");
    } catch (TimeoutException e) {
      log.error("TimeoutException occurred when connecting to broker.", e);
      return false;
    } catch (IOException e) {
      log.error("IOException occurred when connecting to broker.", e);
      return false;
    }
    log.debug("AMQP connected to broker.");
    return true;  
  }

  @Override
  public boolean publish(String message, String topic) {
    try {
      channel.basicPublish(exchangeName, topic, null, message.getBytes("UTF-8"));
    } catch (IOException e) {
      log.error("IOException occurred when publishing message.", e);
      return false;
    }
    return true;
  }

  @Override
  public void subscribe(String topic) {
    try {
      String queueName = channel.queueDeclare().getQueue();
      channel.queueBind(queueName, exchangeName, topic);

      Consumer consumer = new DefaultConsumer(channel) {
        @Override
        public void handleDelivery(String cosumerTag, Envelope envelope,
            AMQP.BasicProperties properties, byte[] body) throws IOException {
          String message = new String(body, "UTF-8");
          dataReceived += message.length();
          log.debug("Data Received: " + dataReceived);
          //log.debug("Received '" + envelope.getRoutingKey() + "':'" + message + "'");
        }
      };

      log.debug("AMQP Listening for messages...");
      channel.basicConsume(queueName, true, consumer);
    } catch (IOException e) {
      log.error("IOException occurred when subscribing to topic.", e);
    }
  }

  @Override
  public void close() {
    try {
      connection.close();
    } catch (IOException e) {
      log.error("IOException occurred when closing connection.", e);
    }
    log.debug("AMQP client disconnected from broker.");
  }

}
