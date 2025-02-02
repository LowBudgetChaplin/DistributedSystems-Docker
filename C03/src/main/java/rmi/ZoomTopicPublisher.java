package rmi;

import org.apache.activemq.ActiveMQConnectionFactory;

import jakarta.jms.BytesMessage;
import jakarta.jms.Connection;
import jakarta.jms.MessageProducer;
import jakarta.jms.Session;
import jakarta.jms.Topic;

import java.nio.file.Files;
import java.nio.file.Paths;

public class ZoomTopicPublisher {
    public static void main(String[] args) {
        try {
            ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://activemq:61616");
            Connection connection = factory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic("ZoomTopic");

            MessageProducer producer = session.createProducer(topic);

            byte[] imageData = Files.readAllBytes(Paths.get("C:\\Users\\serba\\OneDrive\\Desktop\\Master\\Info_economica_master_sem1\\Cloud_Computing_Critoma\\proiect\\test_image.bmp"));

            BytesMessage message = session.createBytesMessage();
            message.writeBytes(imageData);
            producer.send(message);

            System.out.println("Image sent to ZoomTopic");
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

