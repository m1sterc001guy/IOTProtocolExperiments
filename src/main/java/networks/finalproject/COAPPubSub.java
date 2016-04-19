package networks.finalproject;

import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;
import java.util.HashMap;

import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.Utils;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapHandler;

import org.apache.log4j.Logger;

public class COAPPubSub implements IPubSub {

  static Logger log = Logger.getLogger(AMQPPubSub.class.getName());

  private HashMap<String, CoapClient> clients;
  private String brokerHost;
  private CoapObserveRelation relation;
  private long dataReceived;
  private int qos;

  public COAPPubSub(String brokerHost, int qos) {
    dataReceived = 0;
    clients = new HashMap<String, CoapClient>();
    this.brokerHost = brokerHost;
    relation = null;
  }

  @Override
  public boolean connectToBroker() {
    // CoAP is a conenctionless protocol (built on top of UDP), so there
    // is nothing to do for connection setup
    return true;
  }

  @Override
  public boolean publish(String message, String topic) {
    CoapClient client;
    if (clients.containsKey(topic)) {
      client = clients.get(topic);
    } else {
      client = new CoapClient(brokerHost + "/" + topic);
      clients.put(topic, client);
    }
    if (qos == 1) {
      client.useCONs();
    } else {
      client.useNONs();
    }

    client.put(message, 0);
    return true;
  }

  @Override
  public void subscribe(String topic) {

    CoapClient client;
    if (clients.containsKey(topic)) {
      client = clients.get(topic);
    } else {
      client = new CoapClient(brokerHost + "/" + topic);
      clients.put(topic, client);
    }

    relation = client.observe(new CoapHandler() {
      @Override
      public void onLoad(CoapResponse response) {
        dataReceived += response.getResponseText().length();
        log.debug("Data Received: " + dataReceived);
        //log.debug(response.getResponseText()); 
      }

      @Override
      public void onError() {
        log.error("Error occurred while subscribing to a topic (URI)");
      }
    });

    log.debug("Press enter to exit.");
    try {
      System.in.read();
    } catch (IOException e) {
      log.error("IOException occurred when reading from the console.", e);
    }
    log.debug("Data Received: " + dataReceived);
  }

  @Override
  public void close() {
    if (relation != null) {
      relation.proactiveCancel();
    }
  }


}
