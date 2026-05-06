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

        switch (command) {// هنا يستقبل السيرفر الاسم
            case "LOGIN":
                String name = (parts.length > 1) ? parts[1] : "Guest";
                player = new Player(name);
                GameServer.playerManager.addPlayer(player);
                GameServer.registerPlayerWriter(player, out);
                GameServer.broadcastGlobal("PLAYERS_LIST:" + GameServer.playerManager.getConnectedNames());
                break;

            case "PAIR_REQUEST":
                if (player != null) {
                    GameServer.playerManager.addToWaiting(player);

                    GameServer.broadcastGlobal("ROOM_LIST:" + GameServer.playerManager.getWaitingNames());

                    int count = GameServer.playerManager.getWaitingCount();

                    if (count >= 2 && count < 4 && !GameServer.playerManager.isTimerRunning()) {
                        // اذا في لاعب او لاعبين ومرت 30 ثانية تبدا اللعبة
                        GameServer.playerManager.startTimer(30, () -> {
                            List<Player> players = GameServer.playerManager.getAllWaitingPlayers();
                            if (players.size() >= 2) {
                                startGame(players);
                            }
                        });
                    }
                    List<Player> readyPlayers = GameServer.playerManager.tryFormingRoom();
                    if (readyPlayers != null && readyPlayers.size() == 4) {
                        GameServer.playerManager.stopTimer();
                        ;// هنا لو ادخل اربعة لاعبين نوقف المؤقت وتبدا اللعبة فورا

                        startGame(readyPlayers);
                    }
                }
                break;

            case "LEAVE_GAME":
                if (player != null) {// في حال خرج لاعب نعلم البقية انه خرج من اللعبة
                    out.println("LEFT_GAME:You left the game");
                    cleanUp();// نحذف اللاعب من القوائم ونقفل السوكيت
                }
                break;

            default:
                System.out.println("Unknown command: " + command);
                break;
        }
    }

    public void sendMessage(String msg) {
        if (out != null) {
            out.println(msg);
        }
    }

    private void startGame(List<Player> players) {
        // نرسل لكل لاعب رسالة برودكاست لحاله
        for (Player p : players) {
            PrintWriter writer = GameServer.getWriterByPlayer(p);
            if (writer != null) {
                writer.println("GAME_START:Game is starting");
                writer.flush();
            }
        }
        GameServer.broadcastGlobal("ROOM_LIST:" + GameServer.playerManager.getWaitingNames());// قائمة الانتظار تتحدث
                                                                                              // عند الكل
    }

    private void cleanUp() {
        if (player != null) {
            GameServer.unregisterPlayerWriter(player);
            GameServer.playerManager.removePlayer(player.getUsername());
            GameServer.broadcastGlobal("PLAYERS_LIST:" + GameServer.playerManager.getConnectedNames());
            GameServer.broadcastGlobal("ROOM_LIST:" + GameServer.playerManager.getWaitingNames());
        }
        GameServer.removeClient(out);
        try {
            socket.close();
        } catch (IOException e) {
        }
    }
}