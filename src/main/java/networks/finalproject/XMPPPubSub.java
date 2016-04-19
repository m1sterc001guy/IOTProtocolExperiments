package networks.finalproject;

import java.io.IOException;

import rocks.xmpp.core.stanza.model.Message;
import rocks.xmpp.core.session.XmppClient;
import rocks.xmpp.core.XmppException;
import rocks.xmpp.core.session.TcpConnectionConfiguration;
import rocks.xmpp.core.session.ConnectionConfiguration;
import rocks.xmpp.addr.Jid;

import java.security.GeneralSecurityException;
import javax.net.ssl.SSLContext;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;


public class XMPPPubSub implements IPubSub {

  static Logger log = Logger.getLogger(XMPPPubSub.class.getName());

  private XmppClient xmppClient;
  private String username;
  private String password;
  private boolean isSubscriber;
  private String brokerHost;
  private long dataReceived;
  private int numMessagesReceived;

  public XMPPPubSub(String username, String password, String brokerHost, boolean isSubscriber) {
    this.dataReceived = 0;
    this.numMessagesReceived = 0;
    this.username = username;
    this.password = password;
    this.isSubscriber = isSubscriber;
    this.brokerHost = brokerHost;
    try {
      TcpConnectionConfiguration tcpConfiguration = TcpConnectionConfiguration.builder()
        .hostname(brokerHost)
        .port(5222)
        .sslContext(getTrustAllSslContext())
        .secure(false)
        .build();

      xmppClient = new XmppClient(brokerHost, tcpConfiguration);
    } catch (GeneralSecurityException e) {
      log.error("GeneralSecurityException was thrown when creating the XMPPClient.", e);
    }
    
  }

  @Override
  public boolean connectToBroker() {
    if (!isSubscriber) {
      try {
        xmppClient.connect();
        xmppClient.login(this.username, this.password, "xmpp");
        log.debug("XMPP Connected to broker.");
      } catch (XmppException e) {
        log.error("XMPPException occurred when connecting to broker.", e);
        return false;  
      }
    }
    return true;
  }

  @Override
  public boolean publish(String message, String topic) {
    //log.debug("Sending message...");
    // send a message to the "topic", which is really just a user
    xmppClient.send(new Message(new Jid(topic, brokerHost, "xmpp"), Message.Type.CHAT, message, topic));
    return true;
  }

  @Override
  public void subscribe(String topic) {
    xmppClient.addInboundMessageListener(e -> {
      dataReceived += e.getMessage().getBody().length();
      numMessagesReceived++;
      log.debug("Data Received: " + dataReceived);
      log.debug("Num Messages: " + numMessagesReceived);
      //log.debug("Received '" + topic + "':'" + e.getMessage());
    });

    try {
      xmppClient.connect();
      xmppClient.login(this.username, this.password, "xmpp");
      log.debug("XMPP Listening for message...");
      log.debug("Press enter to exit.");

      System.in.read();
    } catch (XmppException e) {
      log.error("XMPPException occurred when connecting to broker.", e);
    } catch (IOException e) {
      log.error("IOException occurred when reading from the console.", e);
    }
  }

  @Override
  public void close() {
    // TODO: figure out how to close the connection
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
