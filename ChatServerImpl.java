import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServerImpl extends UnicastRemoteObject implements ChatServer {
    private Map<String, ChatClient> clients = new ConcurrentHashMap<>();

    protected ChatServerImpl() throws RemoteException {}

    public void registerClient(String clientId, ChatClient client) {
        clients.put(clientId, client);
        System.out.println(clientId + " registered.");
    }

    public void sendMessage(String senderId, String receiverId, String message) {
        ChatClient receiver = clients.get(receiverId);
        if (receiver != null) {
            try {
                receiver.receiveMessage(senderId, message);
            } catch (RemoteException e) {
                System.out.println("Failed to deliver message.");
            }
        }
    }

    public void sendFile(String senderId, String receiverId, byte[] fileData, String fileName) {
        ChatClient receiver = clients.get(receiverId);
        if (receiver != null) {
            try {
                receiver.receiveFile(senderId, fileData, fileName);
            } catch (RemoteException e) {
                System.out.println("Failed to deliver file.");
            }
        }
    }
}
