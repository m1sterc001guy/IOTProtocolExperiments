package networks.finalproject;

import java.io.IOException;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import org.apache.log4j.Logger;

public class MQTTPubSub implements IPubSub, MqttCallback {

  static Logger log = Logger.getLogger(MQTTPubSub.class.getName());

  private MemoryPersistence memory;
  private MqttClient client;
  private MqttConnectOptions connOpts;
  private int qualityOfService;
  private long dataReceived;
  private int numMessagesReceived;

  public MQTTPubSub(String username, String brokerHost, int qualityOfService) {
    try {
      dataReceived = 0;
      numMessagesReceived = 0;
      this.qualityOfService = qualityOfService;
      memory = new MemoryPersistence();
      String brokerTcpString = "tcp://" + brokerHost + ":1883";
      client = new MqttClient(brokerTcpString, username, memory);
      client.setCallback(this);
      connOpts = new MqttConnectOptions();
      connOpts.setCleanSession(true);
    } catch (MqttException e) {
      log.error("MQTTException occurred when creating the client", e);
    }
    
  }

  @Override
  public boolean connectToBroker() {
    try {
      client.connect(connOpts);
    } catch (MqttException e) {
      log.error("MQTTException occurred when connecting to broker.", e);
      return false;
    }
    log.debug("MQTT Connected to broker.");
    return true;
  }

  @Override
  public boolean publish(String message, String topic) {
    try {
      MqttMessage mqttMessage = new MqttMessage(message.getBytes());
      mqttMessage.setQos(this.qualityOfService);
      client.publish(topic, mqttMessage);
    } catch (MqttException e) {
      log.error("MQTTException occurred when publishing a message.", e);
    }
    return true;
  }

  @Override
  public void subscribe(String topic) {
    try {
      client.subscribe(topic, this.qualityOfService);
    } catch (MqttException e) {
      log.error("MQTTException occurred when subscribing to a topic.", e);
    }

    log.debug("Press enter to exit");
    try {
      System.in.read();
    } catch (IOException e) {
      log.error("IOException occurred when reading from the console.", e);
    }
    close();
  }

  @Override
  public void close() {
    try {
      client.disconnect();
    } catch (MqttException e) {
      log.error("MQTTException occurred when disconnecting from the broker.", e);
    }
    log.debug("MQTT Client disconnected from broker.");
  }

  // implementing the MqttCallback interface

  @Override
  public void connectionLost(Throwable e) {
    log.error("MQTT Connection was lost to the broker.");
  }

  @Override
  public void deliveryComplete(IMqttDeliveryToken token) {
  }

  @Override
  public void messageArrived(String topic, MqttMessage message) throws Exception {
    dataReceived += message.getPayload().length;
    numMessagesReceived++;
    log.debug("Data Received: " + dataReceived);
    //log.debug("Num Messages: " + numMessagesReceived);
    //log.debug("Received '" + topic + "':'" + new String(message.getPayload()) + "'");    
  }
}
