import javax.imageio.IIOException;
import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class TCPConnection {

    private final Socket socket;
    private final Thread rxThread;
    private final TCPConnectionListener eventListener;
    private final BufferedReader in;
    private final BufferedWriter out;

    // Конструктор, який приймає IP-адресу та порт і встановлює з'єднання
    public TCPConnection(TCPConnectionListener eventListener, String ipAddr, int port) throws IOException {
        this(eventListener, new Socket(ipAddr, port));
    }

    // Конструктор, який приймає готовий об'єкт Socket і встановлює з'єднання
    public TCPConnection(TCPConnectionListener eventListener, Socket socket) throws IOException {
        this.eventListener = eventListener;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        rxThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    eventListener.onConnectionReady(TCPConnection.this);
                    while (!rxThread.isInterrupted()){
                        String msg = in.readLine();
                        eventListener.onReceiveString(TCPConnection.this, msg);
                    }
                    String msg = in.readLine();
                } catch (IOException e){
                    eventListener.onExepction(TCPConnection.this, e);
                } finally {
                    eventListener.onDisconnect(TCPConnection.this);
                }

            }
        });
        rxThread.start();
    }

    // Метод для відправлення рядка через з'єднання
    public synchronized void sendString(String value){
        try {
            out.write(value + "\r\n");
            out.flush();
        } catch (IOException e){
            eventListener.onExepction(TCPConnection.this, e);
            disconnect();
        }

    }

    // Метод для відключення з'єднання
    private synchronized void disconnect(){
        rxThread.interrupt();
        try {
            socket.close();
        } catch (IOException e){
            eventListener.onExepction(TCPConnection.this, e);
        }

    }

    @Override
    public String toString(){
        return "TCPConnection: " + socket.getInetAddress() + ": " + socket.getPort();
    }
}
