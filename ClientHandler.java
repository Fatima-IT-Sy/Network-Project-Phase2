/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import java.io.*;
import java.net.*;
import java.util.List;

public class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Player player;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            GameServer.addClientWriter(out);

            String request;
            while ((request = in.readLine()) != null) {
                handleProtocol(request);
            }
        } catch (IOException e) {
            System.out.println("⚠️ Player disconnected");
        } finally {
            cleanUp();
        }
    }
private void handleProtocol(String request) {
    String[] parts = request.split(":", 2);
    String command = parts[0];

    switch (command) {
        case "LOGIN":
            String name = (parts.length > 1) ? parts[1] : "Guest";
            player = new Player(name);
            GameServer.playerManager.addPlayer(player);
            
            GameServer.broadcastGlobal("PLAYERS_LIST:" + GameServer.playerManager.getConnectedNames());
            break;

        case "PAIR_REQUEST":
            if (player != null) {
                GameServer.playerManager.addToWaiting(player);
  
                GameServer.broadcastGlobal("ROOM_LIST:" + GameServer.playerManager.getWaitingNames());
                
                List<Player> readyPlayers = GameServer.playerManager.tryFormingRoom();
                if (readyPlayers != null && readyPlayers.size() == 4 ) {
                    GameServer.broadcastGlobal("GAME_START:Ready to Race!");
                }
            }
            break;
    }
}
public void sendMessage(String msg) {
    if (out != null) {
        out.println(msg);
    }
}

    private void cleanUp() {
        if (player != null) {
            GameServer.playerManager.removePlayer(player.getUsername());
            GameServer.broadcastGlobal("PLAYERS_LIST:" + GameServer.playerManager.getConnectedNames());
            GameServer.broadcastGlobal("ROOM_LIST:" + GameServer.playerManager.getWaitingNames());
        }
        GameServer.removeClient(out);
        try { socket.close(); } catch (IOException e) {}
    }
}