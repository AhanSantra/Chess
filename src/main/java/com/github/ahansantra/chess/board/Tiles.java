package com.github.ahansantra.chess.board;

import com.github.ahansantra.chess.pieces.Piece;

public class Tiles {
    private final int row;
    private final int col;
    private Piece piece;

    Tiles(int row, int col, Piece pieceOccupying) {
        this.piece = pieceOccupying;
        this.row = row;
        this.col = col;
    }

    // getters and setters
    public Piece getPiece() {
        return piece;
    }
    public void setPiece(Piece piece) {this.piece = piece;    }
    public int getRow() {
        return row;
    }
    public int getCol() {
        return col;
    }
}
