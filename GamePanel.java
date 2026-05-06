/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    private JTextArea waitingArea = new JTextArea(10, 20);
    private JLabel statusLabel = new JLabel("Waiting for players...");

    public GamePanel() {
        setLayout(new BorderLayout());
        waitingArea.setEditable(false);
        waitingArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        
        add(statusLabel, BorderLayout.NORTH);
        add(new JScrollPane(waitingArea), BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    public void setWaitingList(String players) {
        waitingArea.setText("Waiting Room:\n" + players);
    }

    public void setMessage(String msg) {
        statusLabel.setText(msg);
        statusLabel.setForeground(new Color(0, 102, 204));
    }
}