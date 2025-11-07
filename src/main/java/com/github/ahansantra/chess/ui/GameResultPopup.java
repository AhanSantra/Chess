package com.github.ahansantra.chess.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class GameResultPopup {
    public static void showPopup(JFrame parent, ResultType result, String white, String black, boolean winIsWhite) {
        JDialog dialog = new JDialog(parent, "Result", true);
        dialog.setUndecorated(true);
        dialog.setSize(320, 260);
        dialog.setLayout(new BorderLayout());

        // Rounded corners
        dialog.setShape(new RoundRectangle2D.Double(0, 0, 320, 260, 25, 25));

        // === TOP SECTION (Grey) ===
        JPanel topPanel = new JPanel();
        topPanel.setBackground(Color.DARK_GRAY);
        topPanel.setPreferredSize(new Dimension(320, 120));
        topPanel.setLayout(new BorderLayout());

        // --- Left Icon ---
        JLabel iconLabel = new JLabel();
        try {
            iconLabel.setIcon(new ImageIcon("img/icon.png")); // path to your image
        } catch (Exception e) {
            // fallback if image not found
            iconLabel.setText("♟");
            iconLabel.setFont(new Font("Arial", Font.BOLD, 36));
            iconLabel.setForeground(Color.WHITE);
        }
        iconLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // --- Right Texts ---
        JPanel textPanel = new JPanel();
        textPanel.setBackground(Color.DARK_GRAY);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        // Title text (bold)
        JLabel titleLabel = new JLabel("Game Over");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        // Subtext (normal)
        JLabel subtitleLabel = new JLabel(resultToText(result, white, black, winIsWhite));
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitleLabel.setForeground(Color.LIGHT_GRAY);

        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(subtitleLabel);

        topPanel.add(iconLabel, BorderLayout.WEST);
        topPanel.add(textPanel, BorderLayout.CENTER);

        // === BOTTOM SECTION (Black) ===
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.BLACK);
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Big green main button
        JButton mainButton = new JButton("NEW GAME");
        mainButton.setBackground(new Color(0, 180, 0));
        mainButton.setForeground(Color.WHITE);
        mainButton.setFont(new Font("Arial", Font.BOLD, 16));
        mainButton.setFocusPainted(false);
        mainButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainButton.setMaximumSize(new Dimension(250, 40));

        // Two smaller grey buttons
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonRow.setBackground(Color.BLACK);

        JButton menuButton = new JButton("MAIN MENU");
        JButton exitButton = new JButton("EXIT");

        Color grey = new Color(70, 70, 70);
        for (JButton b : new JButton[]{menuButton, exitButton}) {
            b.setBackground(grey);
            b.setForeground(Color.WHITE);
            b.setFont(new Font("Arial", Font.PLAIN, 13));
            b.setFocusPainted(false);
        }

        buttonRow.add(menuButton);
        buttonRow.add(exitButton);

        bottomPanel.add(mainButton);
        bottomPanel.add(Box.createVerticalStrut(10));
        bottomPanel.add(buttonRow);

        // Add sections to dialog
        dialog.add(topPanel, BorderLayout.NORTH);
        dialog.add(bottomPanel, BorderLayout.CENTER);

        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    // === Helper to generate message text ===
    private static String resultToText(ResultType result, String white, String black, boolean winIsWhite) {
        switch (result) {
            case CHECKMATE:
                return (winIsWhite ? white : black) + " wins by checkmate!";
            case STALEMATE:
                return "Game drawn by stalemate.";
            case THREEFOLD:
                return "Draw by threefold repetition.";
            case FIFTYMOVEPAWN:
                return "Draw by 50-move rule.";
            case TIMEOUT:
                return (winIsWhite ? white : black) + " wins on time!";
            case RESIGNATION:
                return (winIsWhite ? white : black) + " wins by resignation.";
            default:
                return "Game ended.";
        }
    }

    public enum ResultType {
        CHECKMATE,
        STALEMATE,
        THREEFOLD,
        FIFTYMOVEPAWN,
        TIMEOUT,
        RESIGNATION
    }
}