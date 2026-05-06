/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
public class Player {
    private String username;
    private int score = 0;
    public Player(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
  
public void addScore(int points) { this.score += points; }
public int getScore() { return score; }
}