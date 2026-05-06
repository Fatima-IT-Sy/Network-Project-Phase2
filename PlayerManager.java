
import java.util.*;
import java.util.concurrent.*;

public class PlayerManager {
    private final Map<String, Player> activePlayers = new ConcurrentHashMap<>();// تسمح للthreads تشتغل بدون تداخل
    private final Queue<Player> waitingQueue = new ConcurrentLinkedQueue<>();
    private Timer gameTimer;
    private boolean timerRunning = false;

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
        if (waitingQueue.size() >= 4) {// هنا يشيك لوالعدد وصل اربعة بعدها بيمنع الدخول
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

    // الدوال حقت المؤقت
    public int getWaitingCount() {
        return waitingQueue.size();
    }

    public List<Player> getAllWaitingPlayers() {
        return new ArrayList<>(waitingQueue);
    }

    public boolean isTimerRunning() {
        return timerRunning;
    }

    public void startTimer(int seconds, Runnable onFinish) {
        if (gameTimer != null) {// اذا في مؤقت قديم نلغيه
            gameTimer.cancel();
        }
        timerRunning = true;
        gameTimer = new Timer();
        gameTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                timerRunning = false;
                onFinish.run();
            }
        }, seconds * 1000L); // L عشان تكون Long
    }

    public void stopTimer() {// نوقف المؤقت لو اكتملوا 4 لاعبين قبل 30 ثانية
        if (gameTimer != null) {
            gameTimer.cancel();// نلغي المؤقت
            gameTimer = null;
        }
        timerRunning = false;
    }
}