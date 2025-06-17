import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Client extends UnicastRemoteObject implements ChatClient {
    private String clientId;
    private ChatServer server;
    private JTextArea chatArea;
    private JTextField inputField, receiverField;

    protected Client(String clientId) throws RemoteException {
        this.clientId = clientId;
        try {
            server = (ChatServer) Naming.lookup("rmi://localhost:1099/chat");
            server.registerClient(clientId, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        createGUI();
    }

    private void createGUI() {
        JFrame frame = new JFrame("RMI Chat - " + clientId);
        chatArea = new JTextArea(15, 40);
        chatArea.setEditable(false);
        inputField = new JTextField(30);
        receiverField = new JTextField(10);
        JButton sendButton = new JButton("Send");
        JButton fileButton = new JButton("Send File");

        sendButton.addActionListener(e -> {
            try {
                String msg = inputField.getText();
                String receiver = receiverField.getText();
                server.sendMessage(clientId, receiver, msg);
                chatArea.append("Me to " + receiver + ": " + msg + "\n");
                inputField.setText("");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        fileButton.addActionListener(e -> {
            try {
                JFileChooser chooser = new JFileChooser();
                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    byte[] fileData = Files.readAllBytes(file.toPath());
                    String receiver = receiverField.getText();
                    server.sendFile(clientId, receiver, fileData, file.getName());
                    chatArea.append("File sent to " + receiver + ": " + file.getName() + "\n");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        JPanel panel = new JPanel();
        panel.add(new JLabel("To:"));
        panel.add(receiverField);
        panel.add(inputField);
        panel.add(sendButton);
        panel.add(fileButton);

        frame.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        frame.add(panel, BorderLayout.SOUTH);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public void receiveMessage(String senderId, String message) {
        chatArea.append(senderId + ": " + message + "\n");
    }

    public void receiveFile(String senderId, byte[] fileData, String fileName) {
        try {
            File file = new File("received_" + fileName);
            FileOutputStream out = new FileOutputStream(file);
            out.write(fileData);
            out.close();
            chatArea.append("Received file from " + senderId + ": " + fileName + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String clientId = JOptionPane.showInputDialog("Enter your unique ID:");
        try {
            new Client(clientId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
