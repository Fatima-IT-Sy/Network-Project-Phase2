
public class Player {
    private String username;
    private int score = 0;

    public Player(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void addScore(int points) {
        this.score += points;
    }

    public int getScore() {
        return score;
    }
}