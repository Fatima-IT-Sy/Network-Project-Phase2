import java.util.*;

public class SyncManager {
    private final List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());

    public void addClient(ClientHandler client) {
        clients.add(client);
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    
    public void broadcast(String message) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.sendMessage(message);
            }
        }
    }
}