
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class Klient extends JFrame {
    private Socket socket;
    private String användarNamn;
    private JTextArea chatTextArea;
    private JTextField messageTextField;
    private JButton sendButton;
    private JButton disconnectButton;
    private PrintWriter ut;
    public Klient() {
        super("Chatt");
        användarNamn = JOptionPane.showInputDialog("Ange användarnamn: ");
        chatTextArea = new JTextArea();
        chatTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatTextArea);
        messageTextField = new JTextField();
        sendButton = new JButton("Skicka");
        disconnectButton = new JButton("Koppla ner");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                skickaMeddelanden();
            }
        });
        disconnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                kopplaIfrån();
            }
        });
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.add(messageTextField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        inputPanel.add(disconnectButton, BorderLayout.WEST);
        add(inputPanel, BorderLayout.SOUTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 350);
        setVisible(true);

        /*  ett  användargränssnitt skapas med ett fönster där användaren kan skicka meddelanden
            och se inkommande meddelanden från andra användare.
            actionlisteners:
            skickaMeddelanden() och kopplaIfrån()
        */

        try {
            socket = new Socket("127.0.0.1", 55555);
            ut = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Thread receiveThread = new Thread(new Meddelanden(socket, chatTextArea));
        receiveThread.start();
    }
    /*
      skapar en socket anslutning till serern och en printwriter för att skicka meddelanden.
      ny tråd skapas för att lyssna på inkommande meddelanden från servern
     */
    private void skickaMeddelanden() {
        String meddelanden = messageTextField.getText();
        ut.println(användarNamn + ": " + meddelanden);
        messageTextField.setText("");
    }
    private void kopplaIfrån() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Klient();
            }
        });
    }
}