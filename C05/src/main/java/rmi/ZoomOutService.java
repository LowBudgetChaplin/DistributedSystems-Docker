package rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ZoomOutService extends UnicastRemoteObject implements ZoomService {

    protected ZoomOutService() throws RemoteException {
        super();
    }

    @Override
    public byte[] processImage(byte[] imageData, int zoomPercentage) throws RemoteException {
        System.out.println("Zoom Out Service: Received request to zoom out by " + zoomPercentage + "%");
        return imageData;
    }
}