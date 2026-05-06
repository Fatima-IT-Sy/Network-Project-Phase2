/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame implements GameClient.MessageListener {
    private GameClient client;
    private DefaultListModel<String> allPlayersModel = new DefaultListModel<>();
    private DefaultListModel<String> waitingRoomModel = new DefaultListModel<>();
    private JButton joinBtn;
    private JLabel statusLabel;
    private JPanel mainPanel;
    private DefaultListModel<String> scoreBoardModel = new DefaultListModel<>();

    public GameFrame(String username) {
        mainPanel = new JPanel();
        setTitle("Cipher Race - Session: " + username);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        getContentPane().setBackground(new Color(10, 10, 10));
        setLayout(new BorderLayout(20, 20));

        statusLabel = new JLabel(username.toUpperCase());
        statusLabel.setForeground(new Color(0, 255, 65));
        statusLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        add(statusLabel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 25, 0));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        mainPanel.add(createListPanel("CONNECTED CLIENTS", new JList<>(allPlayersModel)));
        mainPanel.add(createListPanel("WAITING ROOM", new JList<>(waitingRoomModel)));

        add(mainPanel, BorderLayout.CENTER);

        joinBtn = new JButton("JOIN WAITING ROOM (PLAY)");
        joinBtn.setBackground(new Color(0, 40, 0));
        joinBtn.setForeground(new Color(0, 255, 65));
        joinBtn.setFont(new Font("Monospaced", Font.BOLD, 16));
        joinBtn.setFocusPainted(false);
        joinBtn.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 65), 1));
        joinBtn.setPreferredSize(new Dimension(0, 50));

        JPanel btnPanel = new JPanel(new BorderLayout());
        btnPanel.setOpaque(false);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        btnPanel.add(joinBtn, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        client = new GameClient("localhost", 12345, this);

        Timer t = new Timer(500, e -> client.sendMessage("LOGIN:" + username));
        t.setRepeats(false);
        t.start();

        joinBtn.addActionListener(e -> {
            client.sendMessage("PAIR_REQUEST");
            setWaitingMode();
        });

    }

    private JPanel createListPanel(String title, JList<String> list) {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setOpaque(false);

        JLabel lbl = new JLabel(title);
        lbl.setForeground(Color.GRAY);
        lbl.setFont(new Font("Monospaced", Font.PLAIN, 12));
        p.add(lbl, BorderLayout.NORTH);

        list.setBackground(new Color(20, 20, 20));
        list.setForeground(new Color(0, 255, 65));
        list.setFont(new Font("Monospaced", Font.PLAIN, 14));
        list.setSelectionBackground(new Color(0, 60, 0));

        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(40, 40, 40), 1));
        p.add(scroll, BorderLayout.CENTER);

        return p;
    }

    private void setWaitingMode() {
        joinBtn.setEnabled(false);
        joinBtn.setText("SEARCHING FOR TARGET...");
        joinBtn.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 1));
        statusLabel.setText("MATCHING PROCESS...");
        statusLabel.setForeground(Color.ORANGE);
    }

    @Override
    public void onMessageReceived(String msg) {
        System.out.println("CLIENT_LOG: Received -> " + msg);

        SwingUtilities.invokeLater(() -> {
            if (msg.startsWith("PLAYERS_LIST:")) {
                allPlayersModel.clear();
                String content = msg.substring(13).trim();
                if (!content.isEmpty()) {
                    for (String s : content.split(",")) {
                        allPlayersModel.addElement("> " + s.trim());
                    }
                }
            } else if (msg.startsWith("ROOM_LIST:")) {
                waitingRoomModel.clear();
                String content = msg.substring(10).trim();
                if (!content.isEmpty()) {
                    for (String s : content.split(",")) {
                        waitingRoomModel.addElement("READY: " + s.trim());
                    }
                }
            } else if (msg.startsWith("GAME_START:")) {
                JOptionPane.showMessageDialog(this, "CONNECTION ESTABLISHED\nDATA: " + msg.split(":")[1]);
            }
        });
    }

    public void showGamePanel() {
        mainPanel.removeAll();
        mainPanel.setLayout(new BorderLayout());

        JLabel startMsg = new JLabel("GAME STARTED! RACE NOW!", JLabel.CENTER);
        startMsg.setForeground(Color.CYAN);
        startMsg.setFont(new Font("Monospaced", Font.BOLD, 20));

        mainPanel.add(startMsg, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(new JList<>(scoreBoardModel)), BorderLayout.CENTER);

        statusLabel.setText("RACING...");
        revalidate();
        repaint();
    }

    public void refreshScoreBoard(String playerName, String score) {
        scoreBoardModel.addElement(playerName + " : " + score + " PTS");
    }
}