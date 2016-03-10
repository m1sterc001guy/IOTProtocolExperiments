package networks.finalproject;

import java.io.IOException;

import rocks.xmpp.core.stanza.model.Message;
import rocks.xmpp.core.session.XmppClient;
import rocks.xmpp.core.XmppException;
import rocks.xmpp.core.session.TcpConnectionConfiguration;
import rocks.xmpp.core.session.ConnectionConfiguration;

import java.security.GeneralSecurityException;
import javax.net.ssl.SSLContext;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class XmppPublish {

  public static void main(String[] args) throws XmppException, GeneralSecurityException {

    TcpConnectionConfiguration tcpConfiguration = TcpConnectionConfiguration.builder()
      .hostname("192.168.1.228")
      .port(5222)
      .sslContext(getTrustAllSslContext())
      .secure(false)
      .build();
    XmppClient xmppClient = new XmppClient("192.168.1.228", tcpConfiguration);

    xmppClient.addInboundMessageListener(e -> {
      //Message message = e.getMessage();
      System.out.println("Message: " + e.getMessage());
    });

    xmppClient.connect();
    xmppClient.login("raspberrypi", "raspberrypi", "xmpp");

    xmppClient.send(new Message(xmppClient.getConnectedResource(), Message.Type.CHAT, "Hello World! Echo!"));
    
  }

  protected static SSLContext getTrustAllSslContext() throws GeneralSecurityException {
    SSLContext sslContext = SSLContext.getInstance("TLS");
    sslContext.init(null, new TrustManager[] {
      new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
          return new X509Certificate[0];
        }
      }
    }, new SecureRandom());
    return sslContext;
  }

}
