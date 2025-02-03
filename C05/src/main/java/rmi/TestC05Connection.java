package rmi;

import rmi.ZoomService;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class TestC05Connection {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1098); // Use actual IP if remote
            ZoomService zoomOutService = (ZoomService) registry.lookup("ZoomOutService");

            byte[] dummyImage = new byte[10]; // Dummy image data
            byte[] result = zoomOutService.processImage(dummyImage, 20);

            System.out.println("Successfully called ZoomOutService. Result length: " + result.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}