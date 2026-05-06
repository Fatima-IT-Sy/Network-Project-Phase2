import java.io.*;
import java.net.*;
import javax.swing.SwingUtilities;

public class GameClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private MessageListener listener;
    private GameFrame gameFrame;

    public interface MessageListener {
        void onMessageReceived(String msg);
    }

    public GameClient(String host, int port, MessageListener listener) {
        this.listener = listener;
        
        new Thread(() -> {
            try {
                this.socket = new Socket(host, port);
                this.out = new PrintWriter(socket.getOutputStream(), true);
                this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String response;
                while ((response = in.readLine()) != null) {
                    final String msg = response;
                    if (msg.equals("START_GAME")) {
        SwingUtilities.invokeLater(() -> gameFrame.showGamePanel()); 
    } 
    
    else if (msg.startsWith("SCORE_UPDATE:")) {
        String[] parts = msg.split(":");
        SwingUtilities.invokeLater(() -> gameFrame.refreshScoreBoard(parts[1], parts[2]));
    }


                    if (this.listener != null) {
                        this.listener.onMessageReceived(msg);
                    }
                }
            } catch (IOException e) {
                if (this.listener != null) {
                    this.listener.onMessageReceived("ERROR: Connection Lost");
                }
            }
        }).start();
    }

    public void sendMessage(String msg) {
        if (out != null) {
            out.println(msg);
        }
    }
}