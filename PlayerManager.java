
import java.util.*;
import java.util.concurrent.*;

public class PlayerManager {
    private final Map<String, Player> activePlayers = new ConcurrentHashMap<>();// تسمح للthreads تشتغل بدون تداخل
    private final Queue<Player> waitingQueue = new ConcurrentLinkedQueue<>();

    public void addPlayer(Player player) {
        activePlayers.put(player.getUsername().toLowerCase(), player);
    }
    // ونضيفه في قائة المتصلين

    public void addToWaiting(Player player) {
        if (!waitingQueue.contains(player)) {

            waitingQueue.add(player);
        }
    }

    public List<Player> tryFormingRoom() {
        if (waitingQueue.size() >= 4) {// هنا يشيك لوالعدد وص اربعة بعدها بيمنع الدخول
            List<Player> roomPlayers = new ArrayList<>();// هنا نخزن اللاعبين الاربعة اللي راح يبداون اللعبة
            for (int i = 0; i < 4; i++) {
                roomPlayers.add(waitingQueue.poll());
            }
            return roomPlayers;
        }
        return null;
    }

    public String getConnectedNames() {
        return String.join(",", activePlayers.keySet());
    }

    public String getWaitingNames() {
        List<String> names = new ArrayList<>();
        for (Player p : waitingQueue)
            names.add(p.getUsername());
        return String.join(",", names);
    }

    public void removePlayer(String username) {// هنا ينحذف اللاعب من الذاكرة بعد ما تستدعيها دالة التنظيف
        if (username != null) {
            Player p = activePlayers.remove(username.toLowerCase());
            if (p != null)
                waitingQueue.remove(p);
        }
    }
}