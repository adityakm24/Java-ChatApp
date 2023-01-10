import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import javax.swing.*;

public class ChatApp extends JFrame {
    private JList<String> messageList;
    private DefaultListModel<String> listModel;
    private JTextField messageField;
    private JTextField nameField;
    private JButton sendButton;
    private ArrayList<String> messages;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public ChatApp(String host, int port) {
        // Set up the GUI
        setTitle("Chat App");
        setSize(400, 500);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        listModel = new DefaultListModel<>();
        messageList = new JList<>(listModel);
        messageList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(messageList);
        add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());

        messageField = new JTextField();
        inputPanel.add(messageField, BorderLayout.CENTER);

        nameField = new JTextField("Anonymous");
        inputPanel.add(nameField, BorderLayout.WEST);

        sendButton = new JButton("Send");
        inputPanel.add(sendButton, BorderLayout.EAST);

        add(inputPanel, BorderLayout.SOUTH);

        // Set up the list to store messages
        messages = new ArrayList<>();

        // Connect to the server
        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Add a listener for the send button
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String message = messageField.getText();
                if (name.isEmpty()) {
                    name = "Anonymous";
                }
                if (!message.isEmpty()) {
                    out.println(name + ": " + message);
                    messageField.setText("");
                }
            }
        });

        // Add a listener for incoming messages
        Thread thread = new Thread(new Runnable() {
            public void run() {
                String message;
                try {
                    while ((message = in.readLine()) != null) {
                        messages.add(message);
                        listModel.addElement(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        // Show the GUI
        setVisible(true);
    }

    public static void main(String[] args) {
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        ChatApp chatApp = new ChatApp(host, port);
    }
}