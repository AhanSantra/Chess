package com.github.ahansantra.chess.ui;

import javax.swing.*;
import java.awt.*;

public class PromotionWindow extends JDialog {

    private String chosenPiece = null;

    public PromotionWindow(JFrame parent, boolean isWhite) {
        super(parent, "Promote Pawn", true);
        setLayout(new GridLayout(1, 4, 10, 10));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(400, 120);
        setLocationRelativeTo(parent);

        String color = isWhite ? "white" : "black";
        String basePath = "img/" + color + "/";

        addOption("Queen", basePath + "queen.png");
        addOption("Rook", basePath + "rook.png");
        addOption("Bishop", basePath + "bishop.png");
        addOption("Knight", basePath + "knight.png");
    }

    private void addOption(String name, String iconPath) {
        ImageIcon icon = new ImageIcon(iconPath);
        Image scaled = icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
        JButton button = new JButton(new ImageIcon(scaled));
        button.setToolTipText(name);
        button.addActionListener(_ -> {
            chosenPiece = name;
            dispose();
        });
        add(button);
    }

    public String getChosenPiece() {
        return chosenPiece;
    }

    public static String showDialog(JFrame parent, boolean isWhite) {
        PromotionWindow dialog = new PromotionWindow(parent, isWhite);
        dialog.setVisible(true);
        return dialog.getChosenPiece();
    }
}
