package networks.finalproject;

public interface IPubSub {

  public boolean connectToBroker();
  public boolean publish(String message, String topic);
  public void subscribe(String topic);
  public void close();
}
