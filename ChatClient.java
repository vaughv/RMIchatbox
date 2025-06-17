import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatClient extends Remote {
    void receiveMessage(String senderId, String message) throws RemoteException;
    void receiveFile(String senderId, byte[] fileData, String fileName) throws RemoteException;
}
