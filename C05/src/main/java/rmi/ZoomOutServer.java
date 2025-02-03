package rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import shared.ZoomService;

public class ZoomOutServer extends UnicastRemoteObject implements ZoomService {

    protected ZoomOutServer() throws RemoteException {
        super();
    }

    @Override
    public byte[] processImage(byte[] imageData, int zoomPercentage) throws RemoteException {
        try {
            System.out.println("Zoom Out Server processing image...");
            return ImageUtils.zoomOut(imageData, zoomPercentage);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException("Error processing image: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            System.setProperty("java.rmi.server.hostname", "c05_zoom_out_server");
            Registry registry = LocateRegistry.createRegistry(1098);
            ZoomOutServer server = new ZoomOutServer();
            registry.rebind("ZoomOutService", server);
            System.out.println("Zoom Out Server is running...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}