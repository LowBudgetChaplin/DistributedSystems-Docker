package backend;

import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.BytesMessage;

@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = "ZoomTopic"),
    @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
    @ActivationConfigProperty(propertyName = "connectionFactoryLookup", propertyValue = "java:/ConnectionFactory")
})
public class ZoomTopicListener implements MessageListener {

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof BytesMessage) {
                BytesMessage bytesMessage = (BytesMessage) message;
                byte[] fileContent = new byte[(int) bytesMessage.getBodyLength()];
                bytesMessage.readBytes(fileContent);

                System.out.println("Received BMP file with size: " + fileContent.length + " bytes");
            } else {
                System.out.println("Unknown message type received.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}