package networks.finalproject;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.Utils;
import org.eclipse.californium.core.CoapClient;

public class CoapPublish {


  public static void main(String[] args) {
    URI uri = null;
    if (args.length > 0) {
      try {
        uri = new URI(args[0]);
      } catch (URISyntaxException e) {
        System.err.println("Invalid URI: " + e.getMessage());
        System.exit(-1);
      }

      CoapClient client = new CoapClient(uri);
      CoapResponse response = client.get();

      if (response != null) {
        //System.out.println("Code: " + response.getCode());
        //System.out.println("ResponseOptions: " + response.getOptions());
        //System.out.println("Response Text: " + response.getResponseText());

        System.out.println(Utils.prettyPrint(response));
      } else {
        System.out.println("No response received.");
      }
    }
  }

}
