package ie.ucd.contactsDiscovery;

import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.Message;
import javax.jms.TextMessage;

public class ContactsDiscoveryListener implements MessageListener {
    public void onMessage(Message message) {
         if (message instanceof TextMessage) {
            try {
                System.out.println(((TextMessage) message).getText());
            } catch (JMSException ex) {
                throw new RuntimeException(ex);
            }
        } else {
             System.out.println("Message must be of type TextMessage");
        }
    }
}