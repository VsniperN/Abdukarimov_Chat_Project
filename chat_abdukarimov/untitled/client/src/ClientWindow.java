import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ClientWindow extends JFrame implements ActionListener, TCPConnectionListener {

    private static final String IP_ADDR = "192.168.0.104";
    private static final int PORT = 8080;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientWindow();
            }
        });
    }

    private final JTextArea log = new JTextArea();
    private final JTextField fieldNickname = new JTextField(("Fox"));
    private final JTextField fieldInput = new JTextField();

    private TCPConnection connection;

    private ClientWindow() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);

        log.setEditable(false);
        log.setLineWrap(true);
        log.setForeground(Color.YELLOW);
        log.setBackground(Color.BLUE);
        add(new JScrollPane(log), BorderLayout.CENTER); // Додали JScrollPane для JTextArea log

        Font font = log.getFont();
        Font newFont = font.deriveFont(font.getSize() + 3f); // Збільшити розмір шрифту на 3 пункти
        log.setFont(newFont);

        fieldInput.addActionListener(this);
        add(fieldInput, BorderLayout.SOUTH);
        add(fieldNickname, BorderLayout.NORTH);

        // Збільшення розміру шрифту для поля вводу тексту
        Font inputFont = fieldInput.getFont();
        Font newInputFont = inputFont.deriveFont(inputFont.getSize() + 8f); // Збільшити розмір шрифту на 8 пунктів
        fieldInput.setFont(newInputFont);

        setVisible(true);
        try {
            connection = new TCPConnection(this, IP_ADDR, PORT);
        } catch (IOException e) {
            printMessage("Connection exception: " + e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Обробка події введення тексту та надсилання повідомлення
        String message = fieldInput.getText();
        if (message.equals("")) return; // Якщо введений текст пустий, то не виконувати подальші дії
        fieldInput.setText(null); // Очистити поле вводу тексту
        connection.sendString(fieldNickname.getText() + ": " + message); // Надіслати повідомлення через TCP з'єднання
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        // Обробка події підключення TCP з'єднання
        printMessage("Connection ready...");
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String value) {
        // Обробка події отримання повідомлення по TCP з'єднанню
        printMessage(value);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        // Обробка події відключення TCP з'єднання
        printMessage("Connection close");
    }

    @Override
    public void onExepction(TCPConnection tcpConnection, Exception e) {
        // Обробка події виникнення виключення при роботі з TCP з'єднанням
        printMessage("Connection exception: " + e);
    }

    private synchronized void printMessage(String message) {
        // Метод для виведення повідомлення в JTextArea
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Додавання повідомлення в JTextArea з роздільником
                log.append("\n--------------------------------------------------------------------------------------------------------------------\n");
                log.append(message);
                log.append("\n--------------------------------------------------------------------------------------------------------------------\n");
                log.setCaretPosition(log.getDocument().getLength());
            }
        });
    }
}

