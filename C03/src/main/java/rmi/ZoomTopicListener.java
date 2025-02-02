package rmi;

import shared.ImageProcessor;
import shared.ZoomService;
import org.apache.activemq.ActiveMQConnectionFactory;
import jakarta.jms.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ZoomTopicListener {
    private static final String RMI_SERVER_C04 = "c04_zoom_in_server";
    private static final int C04_PORT = 1099;
    private static final String RMI_SERVER_C05 = "c05_zoom_out_server";
    private static final int C05_PORT = 1098;
    private static final String SAVE_DIRECTORY = "/app/processed-images/";

    public static void main(String[] args) {
        try {
            System.out.println("Starting ZoomTopicListener...");

            Connection connection = null;
            int retries = 10;
            while (retries-- > 0 && connection == null) {
                try {
                    ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://activemq:61616");
                    connection = factory.createConnection();
                    connection.start();
                    System.out.println("Connected to ActiveMQ broker.");
                } catch (Exception e) {
                    System.out.println("ActiveMQ not ready, retrying in 3 seconds...");
                    Thread.sleep(3000);
                }
            }
            if (connection == null) {
                throw new RuntimeException("Could not connect to ActiveMQ.");
            }

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic("ZoomTopic");
            MessageConsumer consumer = session.createConsumer(topic);

            consumer.setMessageListener(message -> {
                try {
                    if (message instanceof BytesMessage) {
                        BytesMessage bytesMessage = (BytesMessage) message;
                        byte[] imageData = new byte[(int) bytesMessage.getBodyLength()];
                        bytesMessage.readBytes(imageData);
                        System.out.println("Received image from ZoomTopic.");

                        processImage(imageData);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            System.out.println("Waiting for messages...");
            Thread.sleep(Long.MAX_VALUE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void processImage(byte[] imageData) throws Exception {
        BufferedImage original = ImageIO.read(new ByteArrayInputStream(imageData));
        int w = original.getWidth();
        int h = original.getHeight();
        
        BufferedImage topHalf = original.getSubimage(0, 0, w, h / 2);
        BufferedImage bottomHalf = original.getSubimage(0, h / 2, w, h / 2);

        byte[] topBytes = convertToBytes(topHalf);
        byte[] bottomBytes = convertToBytes(bottomHalf);

        Registry registryC04 = LocateRegistry.getRegistry(RMI_SERVER_C04, C04_PORT);
        Registry registryC05 = LocateRegistry.getRegistry(RMI_SERVER_C05, C05_PORT);

        ImageProcessor zoomInService = (ImageProcessor) registryC04.lookup("ZoomInServer");
        ZoomService zoomOutService = (ZoomService) registryC05.lookup("ZoomOutService");

        System.out.println("Sending part 1 to ZoomInServer...");
        byte[] processedTop = zoomInService.processImage(topBytes, 20);

        System.out.println("Sending part 2 to ZoomOutServer...");
        byte[] processedBottom = zoomOutService.processImage(bottomBytes, 20);

        BufferedImage merged = mergeImages(processedTop, processedBottom);
        byte[] finalImage = convertToBytes(merged);

        saveImage(finalImage, SAVE_DIRECTORY + "final_image.bmp");
        System.out.println("Image successfully processed and merged.");
    }

    private static byte[] convertToBytes(BufferedImage image) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "bmp", baos);
        return baos.toByteArray();
    }

    private static BufferedImage mergeImages(byte[] topBytes, byte[] bottomBytes) throws Exception {
        BufferedImage top = ImageIO.read(new ByteArrayInputStream(topBytes));
        BufferedImage bottom = ImageIO.read(new ByteArrayInputStream(bottomBytes));

        int w = top.getWidth();
        int h = top.getHeight() + bottom.getHeight();
        BufferedImage merged = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        merged.getGraphics().drawImage(top, 0, 0, null);
        merged.getGraphics().drawImage(bottom, 0, top.getHeight(), null);

        return merged;
    }

    private static void saveImage(byte[] imageData, String filePath) throws Exception {
        Path path = Paths.get(filePath);
        Files.createDirectories(path.getParent());
        Files.write(path, imageData);
        System.out.println("Saved merged image at: " + filePath);
    }
}