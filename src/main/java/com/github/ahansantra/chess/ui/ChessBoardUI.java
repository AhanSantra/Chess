package com.github.ahansantra.chess.ui;

import com.github.ahansantra.chess.board.Board;
import com.github.ahansantra.chess.board.Tiles;
import com.github.ahansantra.chess.pieces.*;
import com.github.ahansantra.chess.utils.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChessBoardUI {
    public static Board gameBoard; // accessed by Main.java

    private static final int ROWS = 8;
    private static final int COLS = 8;
    private static final int TILE_SIZE = 80;
    private static final String IMG_PATH = "/img/"; // Resource root path
    private static final Map<String, ImageIcon> pieceCache = new HashMap<>();

    private static final JLayeredPane[][] tilePanes = new JLayeredPane[ROWS][COLS];
    private static final JLabel[][] pieceLabels = new JLabel[ROWS][COLS];
    private static final OverlayPanel[][] overlays = new OverlayPanel[ROWS][COLS];
    private static final JPanel[][] selectionPanels = new JPanel[ROWS][COLS];

    private static int selectedRow = -1;
    private static int selectedCol = -1;

    private static JFrame frame;

    public static JFrame getFrame() {
        return frame;
    }

    public static void createAndShowGUI() {
        frame = new JFrame("Ahan Chess");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.getContentPane().setBackground(Color.BLACK);

        JPanel container = new JPanel(new GridBagLayout());
        container.setOpaque(false);
        container.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        frame.add(container);

        JPanel boardPanel = new JPanel(new GridLayout(ROWS, COLS));
        boardPanel.setOpaque(false);
        container.add(boardPanel);

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                JLayeredPane tile = new JLayeredPane();
                tile.setPreferredSize(new Dimension(TILE_SIZE, TILE_SIZE));
                tile.setLayout(null);

                JLabel bg = new JLabel();
                bg.setOpaque(true);
                bg.setBounds(0, 0, TILE_SIZE, TILE_SIZE);
                bg.setBackground((row + col) % 2 == 0
                        ? new Color(240, 217, 181)
                        : new Color(181, 136, 99));

                JLabel pieceLabel = new JLabel();
                pieceLabel.setBounds(0, 0, TILE_SIZE, TILE_SIZE);
                pieceLabel.setHorizontalAlignment(JLabel.CENTER);
                pieceLabel.setVerticalAlignment(JLabel.CENTER);

                OverlayPanel overlay = new OverlayPanel();
                overlay.setBounds(0, 0, TILE_SIZE, TILE_SIZE);
                overlay.setVisible(false);

                JPanel selPanel = new JPanel();
                selPanel.setOpaque(false);
                selPanel.setBounds(0, 0, TILE_SIZE, TILE_SIZE);
                selPanel.setBorder(null);
                selPanel.setVisible(true);

                int finalRow = row;
                int finalCol = col;
                tile.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        handleTileClick(finalRow, finalCol);
                    }
                });

                tile.add(bg, Integer.valueOf(0));
                tile.add(pieceLabel, Integer.valueOf(1));
                tile.add(overlay, Integer.valueOf(2));
                tile.add(selPanel, Integer.valueOf(3));

                tilePanes[row][col] = tile;
                pieceLabels[row][col] = pieceLabel;
                overlays[row][col] = overlay;
                selectionPanels[row][col] = selPanel;

                boardPanel.add(tile);
            }
        }

        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Load and cache chess piece images from resources/img/
     */
    private static ImageIcon getPieceIcon(String color, String pieceName) {
        String key = color + "_" + pieceName;
        if (!pieceCache.containsKey(key)) {
            String path = IMG_PATH + color + "/" + pieceName + ".png";
            URL imgURL = ChessBoardUI.class.getResource(path);

            if (imgURL == null) {
                System.err.println("❌ Image not found: " + path);
                return null;
            }

            ImageIcon icon = new ImageIcon(imgURL);
            Image img = icon.getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
            pieceCache.put(key, new ImageIcon(img));
        }
        return pieceCache.get(key);
    }

    private static void handleTileClick(int row, int col) {
        Tiles[][] tiles = gameBoard.getTiles();
        Piece clickedPiece = tiles[row][col].getPiece();

        if (selectedRow == -1 && clickedPiece != null && clickedPiece.isWhite() == gameBoard.isWhiteTurn()) {
            // Select a piece
            selectedRow = row;
            selectedCol = col;
            setSelectionBorder(row, col, true);
            showMoveOverlays(clickedPiece);

        } else if (selectedRow != -1) {
            // Try to move
            int fromRow = selectedRow;
            int fromCol = selectedCol;

            boolean moved = gameBoard.move(fromRow, fromCol, row, col);

            setSelectionBorder(fromRow, fromCol, false);
            clearMoveOverlays();

            long start = System.currentTimeMillis();
            if (moved) {
                selectedRow = -1;
                selectedCol = -1;
                updateBoard(tiles);
                long end = System.currentTimeMillis();
                System.out.println(end-start);
                if (gameBoard.condition != null) {
                    if (gameBoard.condition.equalsIgnoreCase("Stalemate")) {
                        GameResultPopup.showPopup(
                                ChessBoardUI.getFrame(),
                                GameResultPopup.ResultType.STALEMATE,
                                "White",
                                "Black",
                                false,
                                () -> {}
                        );
                    } else if (gameBoard.condition.startsWith("Checkmate_")) {
                        String winner = gameBoard.condition.substring(10);
                        if (winner.equalsIgnoreCase("White")) {
                            GameResultPopup.showPopup(
                                    ChessBoardUI.getFrame(),
                                    GameResultPopup.ResultType.CHECKMATE,
                                    "White",
                                    "Black",
                                    true,
                                    () -> {}
                            );
                        } else if (winner.equalsIgnoreCase("Black")) {
                            GameResultPopup.showPopup(
                                    ChessBoardUI.getFrame(),
                                    GameResultPopup.ResultType.CHECKMATE,
                                    "White",
                                    "Black",
                                    false,
                                    () -> {}
                            );
                        }
                    }
                }

            } else {
                // Move failed — maybe clicked another piece
                if (clickedPiece != null && clickedPiece.isWhite() == gameBoard.isWhiteTurn()) {
                    selectedRow = row;
                    selectedCol = col;
                    setSelectionBorder(row, col, true);
                    showMoveOverlays(clickedPiece);
                } else {
                    selectedRow = -1;
                    selectedCol = -1;
                }
            }
        }
    }

    private static void setSelectionBorder(int r, int c, boolean on) {
        JPanel sel = selectionPanels[r][c];
        if (on) {
            sel.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
        } else {
            sel.setBorder(null);
        }
        sel.revalidate();
        sel.repaint();
    }

    private static void showMoveOverlays(Piece piece) {
        clearMoveOverlays();
        ArrayList<Pair> moves = piece.allMoves(gameBoard.getTiles());

        for (Pair move : moves) {
            int r = move.getRow();
            int c = move.getCol();
            if (r >= 0 && r < ROWS && c >= 0 && c < COLS) {
                OverlayPanel ov = overlays[r][c];

                Tiles targetTile = gameBoard.getTiles()[r][c];
                Color color;
                if (piece instanceof Pawn && (targetTile.getRow() == 0 || targetTile.getRow() == 7)) {
                    color = new Color(200, 200, 0, 140);
                } else if (targetTile.getPiece() != null && targetTile.getPiece().isWhite() != piece.isWhite()) {
                    color = new Color(220, 0, 0, 160); // capture
                } else if (piece instanceof Pawn && ((Pawn) piece).canEnPassant(r, c, gameBoard.getTiles())) {
                    color = new Color(0, 100, 255, 160); // en passant
                } else {
                    color = new Color(0, 180, 0, 140); // normal move
                }

                ov.setDotColor(color);
                ov.setVisible(true);
                ov.repaint();
            }
        }
    }

    private static void clearMoveOverlays() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                overlays[r][c].setVisible(false);
                overlays[r][c].repaint();
            }
        }
    }

    public static void updateBoard(Tiles[][] tiles) {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                JLabel pieceLabel = pieceLabels[row][col];
                pieceLabel.setIcon(null);

                Piece piece = tiles[row][col].getPiece();
                if (piece != null) {
                    String color = piece.isWhite() ? "white" : "black";
                    String pieceName = piece.getName().toLowerCase();
                    ImageIcon icon = getPieceIcon(color, pieceName);
                    if (icon != null)
                        pieceLabel.setIcon(icon);
                }
            }
        }

        for (int r = 0; r < ROWS; r++)
            for (int c = 0; c < COLS; c++)
                tilePanes[r][c].repaint();
    }

    /** Transparent overlay panel to draw colored move dots */
    static class OverlayPanel extends JPanel {
        private Color dotColor = new Color(0, 180, 0, 140);

        OverlayPanel() {
            setOpaque(false);
        }

        void setDotColor(Color c) {
            this.dotColor = c;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (!isVisible()) return;

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int size = 16;
            g2.setColor(dotColor);
            g2.fillOval((getWidth() - size) / 2, (getHeight() - size) / 2, size, size);
        }
    }
}
