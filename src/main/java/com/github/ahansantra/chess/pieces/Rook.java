package com.github.ahansantra.chess.pieces;

import com.github.ahansantra.chess.board.Board;
import com.github.ahansantra.chess.board.Tiles;
import com.github.ahansantra.chess.utils.Pair;

import java.util.ArrayList;

public class Rook extends Piece {
    public boolean hasMoved;
    public Rook(boolean isWhite, int row, int col) {
        super(row, col, isWhite, "Rook");
    }

    @Override
    public ArrayList<Pair> allMoves(Tiles[][] board) {
        ArrayList<Pair> moves = new ArrayList<>();

        int[][] directions = {
                {1, 0},   // right
                {-1, 0},  // left
                {0, 1},   // down
                {0, -1}   // up
        };

        for (int[] dir : directions) {
            int i = row + dir[0];
            int j = col + dir[1];

            while (i >= 0 && i < 8 && j >= 0 && j < 8) {
                Tiles tile = board[i][j];

                if (tile.getPiece() == null) {
                    if (!Board.pinned(board, row, col, i, j)) {
                        moves.add(new Pair(i,j));
                    }
                } else {
                    if (tile.getPiece().isWhite() != this.isWhite && !Board.pinned(board, row, col, i, j)) {
                        moves.add(new Pair(i,j));
                    }
                    break;
                }

                i += dir[0];
                j += dir[1];
            }
        }

        return moves;
    }
}
