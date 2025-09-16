import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class ChatServer extends JFrame {

    private JTextArea textArea;
    private JTextField textField;
    private PrintWriter out;

    public ChatServer() {
        setTitle("Server");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textArea = new JTextArea();
        textArea.setEditable(false);
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        textField = new JTextField();
        add(textField, BorderLayout.SOUTH);

        // Send message when Enter is pressed
        textField.addActionListener(e -> {
            if (out != null) {
                String msg = "Server: " + textField.getText();
                textArea.append(msg + "\n");
                out.println(msg); // send to client
                textField.setText("");
            }
        });

        setVisible(true);

        // Start server thread
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(1234)) {
                textArea.append("Server started... waiting for client...\n");
                Socket socket = serverSocket.accept();
                textArea.append("Client connected!\n");

                out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Continuously read messages from client
                String line;
                while ((line = in.readLine()) != null) {
                    textArea.append(line + "\n");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChatServer::new);
    }
}

