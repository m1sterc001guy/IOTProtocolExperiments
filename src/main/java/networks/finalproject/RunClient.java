package networks.finalproject;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.text.SimpleDateFormat;
import java.security.SecureRandom;
import java.lang.StringBuilder;
import java.io.IOException;
import java.lang.Thread;
import java.lang.InterruptedException;

import org.apache.log4j.Logger;

public class RunClient {

  static Logger log = Logger.getLogger(RunClient.class.getName());
  static final String letters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYXabcdefghijklmnopqrstuvwxyz";
  static SecureRandom rnd = new SecureRandom();

  private static IPubSub protocol = null;
  private static boolean keepSendingMessages = true;
  private static boolean isSwitchOn = true;
  private static long totalDataSent = 0;

  private static TimerTask switchToOnTask;
  private static TimerTask switchToOffTask;

  public static void main(String[] args) throws InterruptedException {
    if (args.length != 6) {
      log.error("Insufficient number of arguments. Quitting...");
      System.exit(-1);
    }

    String protocolType = args[0];
    boolean isSubscriber = Boolean.parseBoolean(args[1]);
    int timeInterval = Integer.parseInt(args[2]);
    int messageSize = Integer.parseInt(args[3]);
    int totalTime = Integer.parseInt(args[4]);
    String model = args[5];


    String brokerHost = "192.168.1.152";
    String brokerHostName = "justinsdell";
    final String topic = "topic";

    if (protocolType.equals("amqp")) {
      protocol = new AMQPPubSub("test", "test", brokerHost, "pubsub");
    } else if (protocolType.substring(0, 4).equals("mqtt")) {
      String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
      int qos = Integer.parseInt(protocolType.substring(4, 5));
      protocol = new MQTTPubSub(timestamp, brokerHost, qos);
    } else if (protocolType.equals("xmpp")) {
      // the broker host here must be the HOSTNAME, not the ip address
      if (isSubscriber) {
        protocol = new XMPPPubSub("topic", "topic", brokerHostName, isSubscriber);
      } else {
        protocol = new XMPPPubSub("raspberrypi", "raspberrypi", brokerHostName, isSubscriber);
      }
    } else if (protocolType.substring(0, 4).equals("coap")) {
      int qos = Integer.parseInt(protocolType.substring(4, 5));
      protocol = new COAPPubSub(brokerHost, qos);
    } else {
      log.debug("Unknown Protocol. Quitting...");
      System.exit(-1);
    }
    
    protocol.connectToBroker();

    if (model.equals("onoff")) {
      onOffModel(isSubscriber, topic, messageSize, totalTime, 10000, 10000);
    } else if (model.equals("telemetry")) {
      telemetryPattern(isSubscriber, topic, timeInterval, messageSize, totalTime);
    } else if (model.equals("fast")) {
      sendMessages(isSubscriber, topic, messageSize, totalTime);
    } else if (model.equals("one")) {
      sendOneMessage(isSubscriber, topic, messageSize); 
    } else {
      log.debug("Invalid Model. Quitting...");
      System.exit(-1);
    }

  }

  private static void onOffModel(boolean isSubscriber, String topic, int messageSize, int totalTime, int onInterval, int offInterval) throws InterruptedException {
    if (isSubscriber) {
      protocol.subscribe(topic);
    } else {
      final String message = getRandomMessage(messageSize);

      long onTs = System.currentTimeMillis();
      long offTs = System.currentTimeMillis();
      long startTs = System.currentTimeMillis();
      long totalTimeMillis = (long) (totalTime * 1000);

      while(keepSendingMessages) {
        if (isSwitchOn) {
          totalDataSent += message.length();
          protocol.publish(message, topic);
          if (onTs + onInterval < System.currentTimeMillis()) {
            log.debug("OFF");
            isSwitchOn = false;
            offTs = System.currentTimeMillis();
          }
        } else {
          // sleep for a period of time to not waste CPU
          log.debug("Sleeping...");
          long sleepTime = (long) (offInterval / 10);
          Thread.sleep(sleepTime);
          if (offTs + offInterval < System.currentTimeMillis()) {
            log.debug("ON");
            isSwitchOn = true;
            onTs = System.currentTimeMillis();
          }
        }
        if (System.currentTimeMillis() > startTs + totalTimeMillis) {
          keepSendingMessages = false;
        }
      }
      protocol.close();
      log.debug("Total Data Sent: " + totalDataSent);

    }
  }

  private static void sendMessages(boolean isSubscriber, String topic, int messageSize, int totalTime) {
    if (isSubscriber) {
      protocol.subscribe(topic);
    } else {
      final String message = getRandomMessage(messageSize);

      long startTs = System.currentTimeMillis();
      long totalTimeMillis = (long) (totalTime * 1000);

      while (keepSendingMessages) {
        totalDataSent += message.length();
        protocol.publish(message, topic);
        if (System.currentTimeMillis() > startTs + totalTimeMillis) {
          keepSendingMessages = false;
        }
      }

      protocol.close();
      log.debug("Total Data Sent: " + totalDataSent);
    }
  }

  // use for debugging
  private static void sendOneMessage(boolean isSubscriber, String topic, int messageSize) {
    if (isSubscriber) {
      protocol.subscribe(topic);
    } else {
      String message = getRandomMessage(messageSize);
      protocol.publish(message, topic);
      protocol.close();
    }
  }

  private static void telemetryPattern(boolean isSubscriber, String topic, int timeInterval, int messageSize, int totalTime) {
    if (isSubscriber) {
      protocol.subscribe(topic);
    } else {
      // TODO: add total data sent here
      final String message = getRandomMessage(messageSize);
      Timer timer = new Timer();
      timer.scheduleAtFixedRate(new TimerTask() {
        @Override
        public void run() {
          protocol.publish(message, topic);
        }
      }, 0, timeInterval);


      timer.schedule(new TimerTask() {
        @Override
        public void run() {
          timer.cancel();
          protocol.close();
        }
      }, totalTime * 1000);
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
