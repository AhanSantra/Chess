package com.github.ahansantra.chess.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class GameResultPopup {

    // Cache for rendered popups
    private static final Map<String, BufferedImage> popupCache = new HashMap<>();

    public static void showPopup(JFrame parent, ResultType result, String white, String black, boolean winIsWhite) {
        String key = result.name() + "_" + (winIsWhite ? "white" : "black");

        // Check cache first
        BufferedImage cached = popupCache.get(key);
        if (cached == null) {
            cached = renderPopupImage(result, white, black, winIsWhite);
            popupCache.put(key, cached);
        }

        // Dialog setup
        JDialog dialog = new JDialog(parent, "Result", true);
        dialog.setUndecorated(true);
        dialog.setSize(320, 260);
        dialog.setShape(new RoundRectangle2D.Double(0, 0, 320, 260, 25, 25));
        dialog.setLocationRelativeTo(parent);

        // Use a single panel to paint the cached popup image
        BufferedImage finalCached = cached;
        JPanel imagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(finalCached, 0, 0, getWidth(), getHeight(), null);
            }
        };
        imagePanel.setLayout(null);

        // Buttons (drawn live, not cached)
        JButton mainButton = createButton("NEW GAME", new Color(0, 180, 0));
        JButton menuButton = createButton("MAIN MENU", new Color(70, 70, 70));
        JButton exitButton = createButton("EXIT", new Color(70, 70, 70));

        mainButton.setBounds(35, 160, 250, 40);

        JPanel smallButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        smallButtons.setOpaque(false);
        smallButtons.setBounds(40, 210, 240, 40);
        smallButtons.add(menuButton);
        smallButtons.add(exitButton);

        imagePanel.add(mainButton);
        imagePanel.add(smallButtons);

        dialog.add(imagePanel);
        dialog.setVisible(true);
    }

    /**
     * Renders the static parts (title, message, background, icon) into an image
     * so it can be cached and reused instantly.
     */
    private static BufferedImage renderPopupImage(ResultType result, String white, String black, boolean winIsWhite) {
        int width = 320, height = 260;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background top (dark grey)
        g2.setColor(Color.DARK_GRAY);
        g2.fillRoundRect(0, 0, width, 120, 25, 25);

        // Background bottom (black)
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 120, width, height - 120);

        // Icon
        try {
            Image img = Toolkit.getDefaultToolkit().getImage("img/icon.png");
            g2.drawImage(img, 20, 20, 64, 64, null);
        } catch (Exception e) {
            g2.setFont(new Font("Arial", Font.BOLD, 40));
            g2.setColor(Color.WHITE);
            g2.drawString("♟", 40, 70);
        }

        // Title
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.setColor(Color.WHITE);
        g2.drawString("Game Over", 100, 55);

        // Subtitle
        g2.setFont(new Font("Arial", Font.PLAIN, 13));
        g2.setColor(Color.LIGHT_GRAY);
        String text = resultToText(result, white, black, winIsWhite);
        g2.drawString(text, 100, 80);

        g2.dispose();
        return image;
    }

    private static JButton createButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Arial", Font.BOLD, 14));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        return b;
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
