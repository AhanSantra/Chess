package com.github.ahansantra.chess.pieces;

import com.github.ahansantra.chess.board.Board;
import com.github.ahansantra.chess.board.Tiles;
import com.github.ahansantra.chess.utils.Pair;

import java.util.ArrayList;

public class Pawn extends Piece {
    public boolean hasMoved = false;
    public boolean hasMovedTwoSteps = false;   // controlled by Board

    public Pawn(boolean isWhite, int row, int col) {
        super(row, col, isWhite, "Pawn");
    }

    @Override
    public ArrayList<Pair> allMoves(Tiles[][] board) {
        ArrayList<Pair> moves = new ArrayList<>();

        int direction = isWhite ? -1 : 1; // white moves up, black moves down
        int forwardRow = row + direction;

        // Single step forward
        if (forwardRow >= 0 && forwardRow < 8
                && board[forwardRow][col].getPiece() == null
                && !Board.pinned(board, row, col, forwardRow, col)) {
            moves.add(new Pair(forwardRow, col));

            // Double step forward
            if (!hasMoved) {
                int doubleRow = row + 2 * direction;
                if (doubleRow >= 0 && doubleRow < 8
                        && board[doubleRow][col].getPiece() == null
                        && !Board.pinned(board, row, col, doubleRow, col)) {
                    moves.add(new Pair(doubleRow, col));
                }
            }
        }

        // Diagonal captures
        int[] dCols = {-1, 1};
        for (int d : dCols) {
            int captureCol = col + d;
            if (captureCol >= 0 && captureCol < 8 && forwardRow >= 0 && forwardRow < 8) {
                Tiles tile = board[forwardRow][captureCol];
                if (tile != null && tile.getPiece() != null
                        && tile.getPiece().isWhite != this.isWhite
                        && !Board.pinned(board, row, col, forwardRow, captureCol)) {

                    moves.add(new Pair(forwardRow, captureCol));
                }
            }
        }

        // En passant
        for (int d : dCols) {
            int captureCol = col + d;
            int targetRow = row + direction;
            if (captureCol >= 0 && captureCol < 8 && targetRow >= 0 && targetRow < 8) {
                Tiles adjTile = board[row][captureCol]; // square beside this pawn
                if (adjTile != null && adjTile.getPiece() != null) {
                    Piece adjPawn = adjTile.getPiece();
                    if (adjPawn.name.equals("Pawn")
                            && adjPawn.isWhite != this.isWhite
                            && ((Pawn) adjPawn).hasMovedTwoSteps
                            && !Board.pinned(board, row, col, targetRow, captureCol)) {
                        moves.add(new Pair(targetRow, captureCol));
                    }
                }
            }
        }

        return moves;
    }

    public boolean canEnPassant(int r, int c, Tiles[][] gameBoard) {
        int currentRow = getRow();
        int currentCol = getCol();
        int direction = isWhite ? -1 : 1; // white moves up, black down

        // En passant only works for diagonal moves
        if (Math.abs(c - currentCol) != 1 || r - currentRow != direction) {
            return false;
        }

        // The adjacent square (same row, left or right) must contain an opposing pawn
        int adjacentCol = c;
        int adjacentRow = currentRow;
        if (adjacentCol < 0 || adjacentCol > 7) return false;

        Piece adjacentPiece = gameBoard[adjacentRow][adjacentCol].getPiece();
        if (!(adjacentPiece instanceof Pawn adjacentPawn)) return false;

        // The adjacent pawn must be of the opposite color
        if (adjacentPawn.isWhite == this.isWhite) return false;

        // It must have just moved two squares forward in the last move
        // (you’re already tracking this in your move() method as `hasMovedTwoSteps`)
        return adjacentPawn.hasMovedTwoSteps;
    }

}
