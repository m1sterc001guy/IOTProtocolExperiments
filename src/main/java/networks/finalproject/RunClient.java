package networks.finalproject;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.text.SimpleDateFormat;
import java.security.SecureRandom;
import java.lang.StringBuilder;
import java.io.IOException;

import org.apache.log4j.Logger;

public class RunClient {

  static Logger log = Logger.getLogger(RunClient.class.getName());
  static final String letters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYXabcdefghijklmnopqrstuvwxyz";
  static SecureRandom rnd = new SecureRandom();

  private static IPubSub protocol = null;

  public static void main(String[] args) {
    if (args.length != 4) {
      log.error("Insufficient number of arguments. Quitting...");
      System.exit(-1);
    }

    String protocolType = args[0];
    boolean isSubscriber = Boolean.parseBoolean(args[1]);
    int timeInterval = Integer.parseInt(args[2]);
    int messageSize = Integer.parseInt(args[3]);


    String brokerHost = "192.168.1.228";
    final String topic = "topic";
    //String topic = "tigers";

    if (protocolType.equals("amqp")) {
      protocol = new AMQPPubSub("test", "test", brokerHost, "pubsub");
    } else if (protocolType.equals("mqtt")) {
      String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
      protocol = new MQTTPubSub(timestamp, brokerHost, 2);
    } else if (protocolType.equals("xmpp")) {
      log.debug("TODO: DEBUG XMPP");
    } else if (protocolType.equals("coap")) {
      protocol = new COAPPubSub(brokerHost);
    } else {
      log.debug("Unknown Protocol. Quitting...");
      System.exit(-1);
    }
    
    protocol.connectToBroker();

    telemetryPattern(isSubscriber, topic, timeInterval, messageSize);
    
    
    // TODO: Is there a way to send a message to a group?
    // username: raspberrypi, password: raspberrypi
    // username: tigers, password: raspberrypi2
    //IPubSub protocol = new XMPPPubSub(username, password, brokerHost, isSubscriber);
  }

  private static void telemetryPattern(boolean isSubscriber, String topic, int timeInterval, int messageSize) {
    if (isSubscriber) {
      protocol.subscribe(topic);
    } else {
      final String message = getRandomMessage(messageSize);
      Timer timer = new Timer();
      timer.scheduleAtFixedRate(new TimerTask() {
        @Override
        public void run() {
          protocol.publish(message, topic);
        }
      }, 0, timeInterval);

      log.debug("Press enter to exit");
      try {
        System.in.read();
      } catch (IOException e) {
        log.error("IOException occurred when reading from the console.", e);
      }

      timer.cancel();

      protocol.close();
    }
  }

  private static String getRandomMessage(int length) {
    StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      sb.append(letters.charAt(rnd.nextInt(letters.length())));
    }
    return sb.toString();
  }

}
