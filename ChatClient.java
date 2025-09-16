import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class ChatClient extends JFrame {

    private JTextArea textArea;
    private JTextField textField;
    private PrintWriter out;
    private BufferedReader in;

    public ChatClient() {
        setTitle("Client");
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
                String msg = "Client: " + textField.getText();
                textArea.append(msg + "\n");
                out.println(msg); // send to server
                textField.setText("");
            }
        });

        setVisible(true);

        // Connect to server in a separate thread
        new Thread(() -> {
            try {
                Socket socket = new Socket("localhost", 1234);
                textArea.append("Connected to server!\n");

                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Continuously read messages from server
                String line;
                while ((line = in.readLine()) != null) {
                    textArea.append(line + "\n");
                }

            } catch (IOException ex) {
                textArea.append("Connection error: " + ex.getMessage() + "\n");
                ex.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChatClient::new);
    }
}

