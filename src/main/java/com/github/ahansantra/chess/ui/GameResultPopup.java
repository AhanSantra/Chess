package com.github.ahansantra.chess.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameResultPopup {

    public static void showPopup(JFrame parent, ResultType result, String white, String black, boolean winIsWhite, Runnable newGame) {
        // === Dialog setup ===
        JDialog dialog = new JDialog(parent, "Result", true);
        dialog.setUndecorated(true);
        dialog.setSize(360, 260);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(parent);

        // Sharp rectangular frame with drop shadow effect
        dialog.getRootPane().setBorder(BorderFactory.createLineBorder(new Color(30, 30, 30), 2));

        // Enable high-quality rendering globally
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        // === TOP SECTION ===
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(59, 58, 58));
        topPanel.setPreferredSize(new Dimension(360, 140));

        // Left Icon
        JLabel iconLabel = new JLabel();
        try {
            iconLabel.setIcon(new ImageIcon("img/icon.png"));
        } catch (Exception e) {
            iconLabel.setText("♟");
            iconLabel.setFont(new Font("SansSerif", Font.BOLD, 48));
            iconLabel.setForeground(Color.WHITE);
        }
        iconLabel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Right Texts
        JPanel textPanel = new JPanel();
        textPanel.setBackground(new Color(59, 58, 58));
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("GAME OVER");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel(resultToText(result, white, black, winIsWhite));
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(180, 180, 180));

        textPanel.add(Box.createVerticalStrut(10));
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(8));
        textPanel.add(subtitleLabel);

        topPanel.add(iconLabel, BorderLayout.WEST);
        topPanel.add(textPanel, BorderLayout.CENTER);

        // === BOTTOM SECTION ===
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(15, 15, 15));
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Main "New Game" button
        JButton mainButton = createButton("NEW GAME", new Color(34, 197, 94));
        mainButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        mainButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                newGame.run();
                dialog.dispose();
            }
        });

        // Smaller buttons
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonRow.setBackground(new Color(15, 15, 15));

        JButton menuButton = createButton("MAIN MENU", new Color(60, 60, 60));
        JButton exitButton = createButton("EXIT", new Color(60, 60, 60));

        buttonRow.add(menuButton);
        buttonRow.add(exitButton);

        bottomPanel.add(mainButton);
        bottomPanel.add(Box.createVerticalStrut(12));
        bottomPanel.add(buttonRow);

        // Combine all
        dialog.add(topPanel, BorderLayout.NORTH);
        dialog.add(bottomPanel, BorderLayout.CENTER);

        dialog.setVisible(true);
    }

    // === Button Styling ===
    private static JButton createButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(130, 38));

        // Subtle hover effect
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                b.setBackground(bg.brighter());
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                b.setBackground(bg);
            }
        });
        return b;
    }

    // === Result Text ===
    private static String resultToText(ResultType result, String white, String black, boolean winIsWhite) {
        return switch (result) {
            case CHECKMATE -> (winIsWhite ? white : black) + " wins by checkmate!";
            case STALEMATE -> "Draw by stalemate.";
            case THREEFOLD -> "Draw by threefold repetition.";
            case FIFTYMOVEPAWN -> "Draw by 50-move rule.";
            case TIMEOUT -> (winIsWhite ? white : black) + " wins on time!";
            case RESIGNATION -> (winIsWhite ? white : black) + " wins by resignation.";
        };
    }

    public enum ResultType {
        CHECKMATE, STALEMATE, THREEFOLD, FIFTYMOVEPAWN, TIMEOUT, RESIGNATION
    }
}
