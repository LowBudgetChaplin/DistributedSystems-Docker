package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ImageProcessor extends Remote {
    byte[] processImage(byte[] imageData, int zoomPercentage) throws RemoteException;
}