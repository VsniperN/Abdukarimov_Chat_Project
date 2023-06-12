import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ChatServer implements TCPConnectionListener{

    public static void main (String[] args){
        new ChatServer();
    }

    private final ArrayList<TCPConnection> connections = new ArrayList<>();

    private ChatServer(){
        System.out.println("Server running...");
        try(ServerSocket serverSocket = new ServerSocket(8080)){
            while (true){
                try{
                    new TCPConnection(this, serverSocket.accept());
                } catch (IOException e){
                    System.out.println("TCPConnection exception: " + e);
                }
            }
        } catch (IOException e){
            throw new RuntimeException(e);
        }

    }


    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        // Обробка події підключення TCP з'єднання
        connections.add(tcpConnection);
        sendToAllConnections("Client connected: " + tcpConnection);
    }

    @Override
    public synchronized void onReceiveString(TCPConnection tcpConnection, String value) {
        // Обробка події отримання повідомлення по TCP з'єднанню
        sendToAllConnections(value);
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        // Обробка події відключення TCP з'єднання
        connections.remove(tcpConnection);
        sendToAllConnections("Client disconnected: " + tcpConnection);
    }

    @Override
    public synchronized void onExepction(TCPConnection tcpConnection, Exception e) {
        // Обробка події виникнення виключення при роботі з TCP з'єднанням
        System.out.println("TCPConnection exception: " + e);
    }

    private void sendToAllConnections(String value){
        // Надсилання повідомлення всім підключеним клієнтам
        System.out.println(value);
        final int cnt = connections.size();
        for (int i = 0; i < cnt; i++) connections.get(i).sendString(value);
    }
}
