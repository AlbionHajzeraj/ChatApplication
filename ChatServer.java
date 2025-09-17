import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class ChatServer extends JFrame {

    private JTextArea textArea;
    private JTextField textField;
    private PrintWriter out;
    private JPanel chatPanel;

    public ChatServer() {
        setTitle("Server");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Chat App", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.BLACK);

        topPanel.add(titleLabel, BorderLayout.CENTER);

        JButton themeButton = new JButton("\uD83C\uDF19");
        themeButton.setFont(new Font("Arial", Font.PLAIN, 16));
        themeButton.setPreferredSize(new Dimension(40, 30));
        themeButton.setFocusPainted(false);
        themeButton.setContentAreaFilled(false);
        themeButton.setBorderPainted(false);

        themeButton.addActionListener(e -> {
            if (chatPanel.getBackground() == Color.WHITE) {
                chatPanel.setBackground(Color.DARK_GRAY);
                topPanel.setBackground(Color.DARK_GRAY);
                titleLabel.setForeground(Color.WHITE);
                for (Component c : chatPanel.getComponents()) {
                    if (c instanceof JLabel) {
                        ((JLabel) c).setForeground(Color.WHITE);
                    }
                }
            } else {
                chatPanel.setBackground(Color.WHITE);
                topPanel.setBackground(Color.WHITE);
                titleLabel.setForeground(Color.BLACK);
                for (Component c : chatPanel.getComponents()) {
                    if (c instanceof JLabel) {
                        ((JLabel) c).setForeground(Color.BLACK);
                    }
                }
            }
        });

        topPanel.add(themeButton, BorderLayout.WEST);

        add(topPanel, BorderLayout.NORTH);


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
            addMessage(msg, true);
            out.println(msg);
            textField.setText("");
        });

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(textField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);


        textField.addActionListener(e -> {
            if (out != null) {
                String time = java.time.LocalTime.now().withNano(0).toString();
                String msg = "Server [" + time + "]: " + textField.getText();
                addMessage(msg, true);
                out.println(msg);
                textField.setText("");
            }

        });

        setVisible(true);





        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(1234)) {
                addMessage("Server started... waiting for client...", true);
                Socket socket = serverSocket.accept();
                addMessage("Client connected!", true);


                out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));


                String line;
                while ((line = in.readLine()) != null) {
                    addMessage(line, false);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }).start();


    }

    private void addMessage(String message, boolean isServer) {
        JLabel msgLabel = new JLabel(message);
        msgLabel.setOpaque(true);
        msgLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        if (isServer) {
            msgLabel.setBackground(new Color(200, 230, 255));
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
        SwingUtilities.invokeLater(ChatServer::new);
    }
}
