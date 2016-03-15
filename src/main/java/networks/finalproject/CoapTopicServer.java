package networks.finalproject;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.EndpointManager;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.core.server.resources.CoapExchange;

import java.util.LinkedList;

public class CoapTopicServer extends CoapServer {

  private static final int COAP_PORT = NetworkConfig.getStandard().getInt(NetworkConfig.Keys.COAP_PORT);

  public static void main(String[] args) {
    try {
      CoapTopicServer server = new CoapTopicServer(args[0]);
      server.addEndpoints();
      server.start();
    } catch (SocketException e) {
      System.err.println("Failed to initialize server"); 
    }
  }

  private void addEndpoints() {
    for (InetAddress addr : EndpointManager.getEndpointManager().getNetworkInterfaces()) {
      if (addr instanceof Inet4Address || addr.isLoopbackAddress()) {
        InetSocketAddress bindToAddress = new InetSocketAddress(addr, COAP_PORT);
        addEndpoint(new CoapEndpoint(bindToAddress));
      }
    }
  }

  public CoapTopicServer(String topic) throws SocketException {
    add(new TopicResource(topic));
  }

  class TopicResource extends CoapResource {
    
    private LinkedList<String> messages;

    public TopicResource(String topic) {
      super(topic);
      getAttributes().setTitle(topic);
      setObservable(true);
      messages = new LinkedList<String>();
    }

    @Override
    public void handleGET(CoapExchange exchange) {
      if (messages.size() > 0) {
        exchange.respond(messages.removeFirst());
      } else {
        exchange.respond("Establising observer.");
      }
    }

    @Override
    public void handlePUT(CoapExchange exchange) {
      exchange.accept();
      messages.addLast(exchange.getRequestText());
      exchange.respond(ResponseCode.CHANGED);
      changed();
    }
  }



}
