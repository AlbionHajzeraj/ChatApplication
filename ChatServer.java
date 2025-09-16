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

        JLabel titleLabel = new JLabel("Chat App", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.BLACK);
        add(titleLabel, BorderLayout.NORTH);

        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        scrollPane.getVerticalScrollBar().setBackground(Color.DARK_GRAY);
        scrollPane.getVerticalScrollBar().setForeground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);



        //dekorime te textarea

        textArea.setBackground(Color.WHITE);
        textArea.setForeground(Color.BLACK);
        textArea.setFont(new Font("Monospaced", Font.BOLD, 14));

        textField = new JTextField();
        add(textField, BorderLayout.SOUTH);

        //dekorime te textfield

        textField.setBackground(Color.WHITE);
        textField.setForeground(Color.BLACK);
        textField.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textField.setCaretColor(Color.WHITE);
        textField.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.GRAY));

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(e -> {
            String msg = "Server: " + textField.getText();
            textArea.append(msg + "\n");
            out.println(msg);
            textField.setText("");
        });

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(textField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        // Send message when Enter is pressed
        textField.addActionListener(e -> {
            if (out != null) {
                String time = java.time.LocalTime.now().withNano(0).toString();
                String msg = "Server [" + time + "]: " + textField.getText();
                textArea.append(msg + "\n");
                out.println(msg);
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

