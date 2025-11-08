package com.github.ahansantra.chess.pieces;

import com.github.ahansantra.chess.board.Board;
import com.github.ahansantra.chess.board.Tiles;
import com.github.ahansantra.chess.utils.Pair;

import java.util.ArrayList;

public class King extends Piece {

    public boolean hasMoved = false; // controlled by Board

    public King(boolean isWhite, int row, int col) {
        super(row, col, isWhite, "King");
    }

    @Override
    public ArrayList<Pair> allMoves(Tiles[][] board) {
        ArrayList<Pair> moves = new ArrayList<>();

        int[][] directions = {
                {-1, -1}, {-1, 0}, {-1, 1},
                {0, -1},           {0, 1},
                {1, -1},  {1, 0},  {1, 1}
        };

        // Normal moves
        for (int[] dir : directions) {
            int i = row + dir[0];
            int j = col + dir[1];

            if (i >= 0 && i < 8 && j >= 0 && j < 8) {
                Tiles tile = board[i][j];
                if (tile.getPiece() == null && !Board.pinned(board, row, col, i, j)) {
                    moves.add(new Pair(i, j));
                } else if (tile.getPiece() != null
                        && tile.getPiece().isWhite() != this.isWhite
                        && !Board.pinned(board, row, col, i, j)) {
                    moves.add(new Pair(i, j));
                }
            }
        }

        // Castling
        if (!hasMoved && !Board.pinned(board, row, col, row, col)) {
            if (canCastle(board, true))  moves.add(new Pair(row, col + 2)); // kingside
            if (canCastle(board, false)) moves.add(new Pair(row, col - 2)); // queenside
        }

        return moves;
    }

    private boolean canCastle(Tiles[][] board, boolean kingSide) {
        int rookCol = kingSide ? 7 : 0;
        int direction = kingSide ? 1 : -1;

        Tiles rookTile = board[row][rookCol];
        if (rookTile.getPiece() == null) return false;
        if (!(rookTile.getPiece() instanceof Rook rook)) return false;
        if (rook.hasMoved || this.hasMoved) return false;

        // Squares between king and rook must be empty
        for (int c = col + direction; kingSide ? c < rookCol : c > rookCol; c += direction) {
            if (board[row][c].getPiece() != null) return false;
        }

        // King cannot pass through check
        for (int c = col; c != col + 3 * direction; c += direction) {
            if (c < 0 || c > 7) break;
            if (Board.pinned(board, row, col, row, c)) return false;
        }

        return true;
    }
}
