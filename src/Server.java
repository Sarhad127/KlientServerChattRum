import java.io.*;
import java.net.*;
import java.util.HashSet;
import java.util.Set;

public class Server {
    private final Set<Klienthanterare> Klienter = new HashSet<>();
    // koden deklarerar en Set med Klienthanterare objekt vilket blir oföränderlig för att hålla reda på de
// anslutna klienter, final för att denna snutt inte ska kunna ändras i server koden
    public static void main(String[] args) {
        new Server().startServer(); //varje gång main körs startas en ny server
    }
    private void startServer() {
        System.out.println("Servern är igång");
        try (ServerSocket serverSocket = new ServerSocket(55555)) {
            while (true) {
                Socket klientSocket = serverSocket.accept();
                Klienthanterare klienthanterare = new Klienthanterare(klientSocket);
                Klienter.add(klienthanterare);
                klienthanterare.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /* startServer() ansvarig för att starta och hantera servern genom att acceptera anslutningar från klienter
       och skapa en ny tråd (Klienthanterare) för varje ansluten klient
    */
    private class Klienthanterare extends Thread {
        private final Socket socket;
        private PrintWriter ut;
        private String username;
        public Klienthanterare(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                ut = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                username = in.readLine();
                broadcast(username + " har anslutit.");
                String meddelanden;
                while ((meddelanden = in.readLine()) != null) {
                    broadcast(meddelanden);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                Klienter.remove(this);
                broadcast(username + " har kopplat ner.");
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        /* kodblocken hanterar kommunikationen med en enskild klient, den läser meddelanden från klienten
           och skickar meddelandet till andra anslutna klienter samt dom den
           reagerar på händelser som att en klient ansluter eller kopplas ner
        */
        private void broadcast(String meddelanden) {
            for (Klienthanterare klienter : Klienter) {
                klienter.ut.println(meddelanden);
            }
            /* När metoden broadcast anropas tar den ett meddelande som argument och intererar igenom
               alla anslutna klienter och skickar sedan meddelandet till alla anslutna klienter.
            */
        }
    }
}
