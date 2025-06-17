import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatServer extends Remote {
    void sendMessage(String senderId, String receiverId, String message) throws RemoteException;
    void sendFile(String senderId, String receiverId, byte[] fileData, String fileName) throws RemoteException;
    void registerClient(String clientId, ChatClient client) throws RemoteException;
}
