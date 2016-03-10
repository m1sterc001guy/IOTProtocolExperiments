package networks.finalproject;

import org.apache.log4j.Logger;

public class RunClient {

  static Logger log = Logger.getLogger(RunClient.class.getName());

  public static void main(String[] args) {
    if (args.length != 4) {
      log.error("Insufficient number of arguments. Quitting...");
      System.exit(-1);
    }
    String username = args[0];
    String password = args[1];
    String brokerHost = args[2];
    boolean isSubscriber = Boolean.parseBoolean(args[3]);

    String topic = "tigers";

    // begin experiments
    // username: test, password: test
    //IPubSub protocol = new AMQPPubSub(username, password, brokerHost, "pubsub");
    // username: doesn't matter, just needs to be different from other client
    //IPubSub protocol = new MQTTPubSub(username, brokerHost, 2);
    // TODO: Is there a way to send a message to a group?
    // username: raspberrypi, password: raspberrypi
    // username: tigers, password: raspberrypi2
    IPubSub protocol = new XMPPPubSub(username, password, brokerHost, isSubscriber);
    protocol.connectToBroker();

    if (isSubscriber) {
      protocol.subscribe(topic);
    } else {
      protocol.publish("Tigers are cool!", topic);
      protocol.close();
    }
  }

}
