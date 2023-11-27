import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import javax.swing.JTextArea;

public class Meddelanden implements Runnable {
    private Socket socket;
    private JTextArea chatTextArea;
    public Meddelanden(Socket socket, JTextArea chatTextArea) {
        this.socket = socket;
        this.chatTextArea = chatTextArea;
    }
    @Override
    public void run() {
        try (BufferedReader läsMotaggandeMess = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String meddelanden;
            while ((meddelanden = läsMotaggandeMess.readLine()) != null) {
                chatTextArea.append(meddelanden + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
