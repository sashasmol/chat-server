package chatroom;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * SASHA SMOLYANSKY
 */

public class ChatClient extends ChatWindow {

    // GUI Objects
    private JTextField serverTxt;
    private JTextField nameTxt;
    private JButton connectB;
    private JTextField messageTxt;
    private JButton sendB;


    public ChatClient() {
        super();
        this.setTitle("Chat Client");
        printMsg("Chat Client Started.");

        // GUI elements at top of window
        // Need a Panel to store several buttons/text fields
        serverTxt = new JTextField("localhost");
        serverTxt.setColumns(15);
        nameTxt = new JTextField("Name");
        nameTxt.setColumns(10);
        connectB = new JButton("Connect");
        JPanel topPanel = new JPanel();
        topPanel.add(serverTxt);
        topPanel.add(nameTxt);
        topPanel.add(connectB);
        contentPane.add(topPanel, BorderLayout.NORTH);

        // GUI elements and panel at bottom of window
        messageTxt = new JTextField("");
        messageTxt.setColumns(40);
        sendB = new JButton("Send");
        JPanel botPanel = new JPanel();
        botPanel.add(messageTxt);
        botPanel.add(sendB);
        contentPane.add(botPanel, BorderLayout.SOUTH);

        // Resize window to fit all GUI components
        this.pack();

        // Setup the communicator so it will handle the connect button
        Communicator comm = new Communicator();
        connectB.addActionListener(comm);
        sendB.addActionListener(comm);
    }

    /**
     * This inner class handles communication with the server.
     */
    class Communicator implements ActionListener {
        private Socket socket;
        private PrintWriter writer;
        private BufferedReader reader;
        private int port = 2113;
        private boolean isConnected = false;

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            // Activating connect button
            if (actionEvent.getActionCommand().compareTo("Connect") == 0) {
                if (!isConnected) {
                    connect();
                    ServerMessageReader t = new ServerMessageReader(reader);
                    t.start();
                }
            } else if (actionEvent.getActionCommand().compareTo("Send") == 0) {
                sendMsg(messageTxt.getText());
                // Empty the text field
                ChatClient.this.messageTxt.setText("");
            }
        }

        /**
         * Connect to the remote server and setup input/output streams.
         */
        public void connect() {
            try {
                socket = new Socket(serverTxt.getText(), port);
                InetAddress serverIP = socket.getInetAddress();
                printMsg("Connection from " + serverIP);
                writer = new PrintWriter(socket.getOutputStream(), true);
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String name = nameTxt.getText();

                sendMsg(name);
                isConnected = true;

                ChatClient.this.getRootPane().setDefaultButton(sendB);
            } catch (IOException e) {
                printMsg("\nERROR:" + e.getLocalizedMessage() + "\n");
            }
        }

        /**
         * Send a string
         */
        public void sendMsg(String s) {
            writer.println(s);
        }
    }

    public class ServerMessageReader extends Thread {
        private BufferedReader reader;

        public ServerMessageReader(BufferedReader aReader) {
            reader = aReader;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    readMsg();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // read message from client
        public void readMsg() throws IOException {
            String str = reader.readLine();
            printMsg(str);
            printMsg(getNow());
        }
    }

    public static void main(String args[]) {
        new ChatClient();
    }
}