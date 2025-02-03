package rmi;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;

public class ImageUtils {
    public static byte[] zoomIn(byte[] imageData, int zoomPercentage) throws Exception {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
        int newWidth = image.getWidth() + (image.getWidth() * zoomPercentage / 100);
        int newHeight = image.getHeight() + (image.getHeight() * zoomPercentage / 100);

        BufferedImage zoomedImage = new BufferedImage(newWidth, newHeight, image.getType());
        zoomedImage.getGraphics().drawImage(image, 0, 0, newWidth, newHeight, null);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(zoomedImage, "bmp", baos);
        return baos.toByteArray();
    }

    public static byte[] zoomOut(byte[] imageData, int zoomPercentage) throws Exception {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
        int newWidth = image.getWidth() - (image.getWidth() * zoomPercentage / 100);
        int newHeight = image.getHeight() - (image.getHeight() * zoomPercentage / 100);

        newWidth = Math.max(newWidth, 1);
        newHeight = Math.max(newHeight, 1);

        BufferedImage zoomedOutImage = new BufferedImage(newWidth, newHeight, image.getType());
        zoomedOutImage.getGraphics().drawImage(image, 0, 0, newWidth, newHeight, null);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(zoomedOutImage, "bmp", baos);
        return baos.toByteArray();
    }
}