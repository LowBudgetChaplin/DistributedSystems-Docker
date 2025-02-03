package rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import shared.ImageProcessor;

public class ZoomInServer extends UnicastRemoteObject implements ImageProcessor {

    protected ZoomInServer() throws RemoteException {
        super();
    }

    @Override
    public byte[] processImage(byte[] imageData, int zoomPercentage) throws RemoteException {
        try {
            System.out.println("Zoom In Server processing image...");
            return ImageUtils.zoomIn(imageData, zoomPercentage);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException("Error processing image: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            System.setProperty("java.rmi.server.hostname", "c04_zoom_in_server");
            Registry registry = LocateRegistry.createRegistry(1099);
            ZoomInServer server = new ZoomInServer();
            registry.rebind("ZoomInServer", server);
            System.out.println("Zoom In Server is running...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}