package com.github.ahansantra.chess;

import com.github.ahansantra.chess.board.Board;
import com.github.ahansantra.chess.ui.*;

import javax.swing.*;

public class Main {
    static void main() {
        SwingUtilities.invokeLater(() -> {
            ChessBoardUI.gameBoard = new Board("white");
            ChessBoardUI.createAndShowGUI();
            ChessBoardUI.updateBoard(ChessBoardUI.gameBoard.getTiles());
        });
    }
}
