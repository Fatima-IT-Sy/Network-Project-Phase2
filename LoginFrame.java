/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JButton connectButton;

    public LoginFrame() {
        setTitle("Cipher Race");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("CIPHER RACE", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 255, 65));
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(titleLabel, gbc);

        JLabel subLabel = new JLabel("TYPE YOUR NAME:", SwingConstants.CENTER);
        subLabel.setForeground(Color.GRAY);
        subLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        gbc.gridy = 1;
        mainPanel.add(subLabel, gbc);

        usernameField = new JTextField(15);
        usernameField.setHorizontalAlignment(JTextField.CENTER);
        usernameField.setBackground(new Color(25, 25, 25));
        usernameField.setForeground(Color.WHITE);
        usernameField.setCaretColor(new Color(0, 255, 65));
        usernameField.setFont(new Font("Monospaced", Font.BOLD, 16));
        usernameField.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 65), 1));
        gbc.gridy = 2;
        mainPanel.add(usernameField, gbc);

        // زر الاتصال
        connectButton = new JButton("CONNECT");
        connectButton.setBackground(new Color(0, 40, 0));
        connectButton.setForeground(new Color(0, 255, 65));
        connectButton.setFont(new Font("Monospaced", Font.BOLD, 14));
        connectButton.setFocusPainted(false);
        connectButton.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 65), 1));
        connectButton.setPreferredSize(new Dimension(0, 40));
        gbc.gridy = 3;
        mainPanel.add(connectButton, gbc);

        add(mainPanel);

        connectButton.addActionListener(e -> handleLogin());
        usernameField.addActionListener(e -> handleLogin());
    }

    private void handleLogin() {// تستقبل الاسم
        String user = usernameField.getText().trim();

        if (!user.isEmpty()) {
            GameFrame lobby = new GameFrame(user);
            lobby.setVisible(true);
            this.dispose();
        } else {
            showError("YOUR NAME IS REQUIRED");
        }
    }

    private void showError(String message) {
        UIManager.put("OptionPane.background", Color.BLACK);
        UIManager.put("Panel.background", Color.BLACK);
        UIManager.put("OptionPane.messageForeground", Color.RED);
        JOptionPane.showMessageDialog(this, message, "SYSTEM ERROR", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}