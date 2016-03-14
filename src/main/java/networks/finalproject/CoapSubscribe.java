package networks.finalproject;

import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;

import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.Utils;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapHandler;

public class CoapSubscribe {

  public static void main(String[] args) {
    
    CoapClient client = new CoapClient("10.160.79.35/helloworld");

    CoapObserveRelation relation = client.observe(new CoapHandler() {
      @Override
      public void onLoad(CoapResponse response) {
        System.out.println(response.getResponseText());
      }

      @Override
      public void onError() {
        System.out.println("Failed");
      }
    });

    System.out.println("Press enter to exit");
    try {
      System.in.read();
    } catch (IOException e) {
      System.out.println("IOException occurred when reading from the console.");
    }

    relation.proactiveCancel();
  }


}
