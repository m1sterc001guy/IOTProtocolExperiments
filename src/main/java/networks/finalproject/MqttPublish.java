package networks.finalproject;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttPublish{

  public static void main(String[] args) {
    String topic = "MQTT Examples";
    String content = "Message from MqttPublishSample";
    int qos = 2;
    String broker = "tcp://192.168.1.228:1883";
    String clientId = "JavaSample";
    MemoryPersistence persistence = new MemoryPersistence();
    try {
      MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
      MqttConnectOptions connOpts = new MqttConnectOptions();
      connOpts.setCleanSession(true);
      System.out.println("Connecting to broker: " + broker);
      sampleClient.connect(connOpts);
      System.out.println("Connected");
      System.out.println("Publishing message: " + content);
      MqttMessage message = new MqttMessage(content.getBytes());
      message.setQos(qos);
      sampleClient.publish(topic, message);
      System.out.println("Message published");
      sampleClient.disconnect();
      System.out.println("Disconnected");
    } catch (MqttException e) {
      e.printStackTrace();
    }
  }

}
