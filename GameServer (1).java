import java.io.*;
import java.net.*;
import java.util.*;

public class GameServer {
    private static final int PORT = 12345; 
    
    public static PlayerManager playerManager = new PlayerManager();
    public static final List<PrintWriter> clientWriters = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("🚀 Cipher Race Server - Phase 1 Active...");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New Connection from: " + socket.getInetAddress());
                
                new Thread(new ClientHandler(socket)).start();
            }
        } catch (IOException e) {
            System.err.println("Server Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void addClientWriter(PrintWriter out) { 
        clientWriters.add(out); 
    }
    
    public static void removeClient(PrintWriter out) { 
        clientWriters.remove(out); 
    }

    public static void broadcastGlobal(String message) {
        synchronized (clientWriters) {
            for (PrintWriter writer : clientWriters) {
                writer.println(message);
                writer.flush();
            }
        }
    }
}