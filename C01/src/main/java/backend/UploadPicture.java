package backend;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import org.apache.activemq.ActiveMQConnectionFactory;

import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.MessageProducer;
import jakarta.jms.Session;
import jakarta.jms.BytesMessage;
import jakarta.jms.Topic;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

@WebServlet("/upload")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 1,
    maxFileSize = 1024 * 1024 * 10,
    maxRequestSize = 1024 * 1024 * 15
)
public class UploadPicture extends HttpServlet {

    private static final String BROKER_URL = "tcp://host.docker.internal:61616";
    private static final String TOPIC_NAME = "ZoomTopic";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter writer = response.getWriter();
        writer.println("<html><body>");
        writer.println("<h1>BMP Image Uploader</h1>");
        writer.println("<p>This servlet only handles POST requests for uploading BMP files.</p>");
        writer.println("<p>To upload a file, use the provided HTML form.</p>");
        writer.println("</body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getContentType() == null || !request.getContentType().startsWith("multipart/form-data")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Form must have enctype=multipart/form-data.");
            return;
        }

        try {
            Part filePart = request.getPart("bmpFile");
            if (filePart == null || filePart.getSize() == 0) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No file uploaded.");
                return;
            }

            String fileName = filePart.getSubmittedFileName();
            long fileSize = filePart.getSize();

            InputStream fileContent = filePart.getInputStream();
            byte[] bytes = fileContent.readAllBytes();

            publishToJmsTopic(bytes);

            response.setContentType("text/html");
            PrintWriter writer = response.getWriter();
            writer.println("<html><body>");
            writer.println("<h1>Upload Success</h1>");
            writer.println("<p>File Name: " + fileName + "</p>");
            writer.println("<p>File Size: " + fileSize + " bytes</p>");
            writer.println("<p>Message published to JMS Topic: " + TOPIC_NAME + "</p>");
            writer.println("</body></html>");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing the upload: " + e.getMessage());
        }
    }

    private void publishToJmsTopic(byte[] fileContent) throws Exception {
        System.out.println("Attempting to connect to ActiveMQ broker at: " + BROKER_URL);

        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);
        try (Connection connection = connectionFactory.createConnection()) {
            connection.start();
            System.out.println("Connected to ActiveMQ broker.");

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic(TOPIC_NAME);
            MessageProducer producer = session.createProducer(topic);

            BytesMessage message = session.createBytesMessage();
            message.writeBytes(fileContent);
            producer.send(message);

            System.out.println("Message published to topic: " + TOPIC_NAME);
        } catch (Exception e) {
            System.err.println("Error while publishing message: " + e.getMessage());
            throw e;
        }
    }
}