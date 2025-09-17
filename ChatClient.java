import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class ChatClient extends JFrame {

    private JTextArea textArea;
    private JTextField textField;
    private PrintWriter out;
    private BufferedReader in;
    private JPanel chatPanel;


    public ChatClient() {
        setTitle("Client");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);



        JLabel titleLabel = new JLabel("Chat App", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.BLACK);
        add(titleLabel, BorderLayout.NORTH);



        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(chatPanel);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        scrollPane.getVerticalScrollBar().setBackground(Color.DARK_GRAY);
        scrollPane.getVerticalScrollBar().setForeground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);





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
            addMessage(msg, false);
            out.println(msg);
            textField.setText("");
        });

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(textField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);



        textField.addActionListener(e -> {
            if (out != null) {
                String msg = "Client: " + textField.getText();
                addMessage(msg, false);
                out.println(msg);
                textField.setText("");
            }
        });

        setVisible(true);

        JButton themeButton = new JButton("Toggle Theme");
        themeButton.addActionListener(e -> {
            if (chatPanel.getBackground() == Color.WHITE) {
                chatPanel.setBackground(Color.DARK_GRAY);
                for (Component c : chatPanel.getComponents()) {
                    if (c instanceof JLabel) {
                        ((JLabel) c).setForeground(Color.WHITE);
                    }
                }
            } else {
                chatPanel.setBackground(Color.WHITE);
                for (Component c : chatPanel.getComponents()) {
                    if (c instanceof JLabel) {
                        ((JLabel) c).setForeground(Color.BLACK);
                    }
                }
            }
        });

        add(themeButton, BorderLayout.NORTH);



        new Thread(() -> {
            try {
                Socket socket = new Socket("localhost", 1234);
                addMessage("Connected!", true);

                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Continuously read messages from server
                String line;
                while ((line = in.readLine()) != null) {
                    addMessage(line, false);
                }

            } catch (IOException ex) {
                textArea.append("Connection error: " + ex.getMessage() + "\n");
                ex.printStackTrace();
            }
        }).start();
    }

    private void addMessage(String message, boolean isServer) {
        JLabel msgLabel = new JLabel(message);
        msgLabel.setOpaque(true);
        msgLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        if (isServer) {
            msgLabel.setBackground(new Color(200, 230, 255)); // light blue
            msgLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        } else {
            msgLabel.setBackground(new Color(220, 220, 220)); // gray
            msgLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        }

        msgLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));

        chatPanel.add(msgLabel);
        chatPanel.revalidate();
        chatPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChatClient::new);
    }
}

