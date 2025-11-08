package com.github.ahansantra.chess.board;

import com.github.ahansantra.chess.pieces.*;
import com.github.ahansantra.chess.ui.ChessBoardUI;
import com.github.ahansantra.chess.ui.PromotionWindow;
import com.github.ahansantra.chess.utils.Pair;

public class Board {
    private final Tiles[][] tiles;
    private final boolean whiteBottom;
    private boolean whiteTurn; // track whose turn it is
    public String condition;

    public Board(String bottomColor) {
        tiles = new Tiles[8][8];
        whiteBottom = bottomColor.equalsIgnoreCase("white");
        whiteTurn = true; // white starts first
        condition = "";
        // Initialize empty tiles
        for (int row = 0; row < 8; row++)
            for (int col = 0; col < 8; col++)
                tiles[row][col] = new Tiles(row, col, null);

        setupPieces();
    }

    public boolean isWhiteTurn() { return whiteTurn; }
    public Tiles[][] getTiles() { return tiles; }
    private void switchTurn() { whiteTurn = !whiteTurn; }

    private void setupPieces() {
        if (whiteBottom) {
            setupBackRank(7, true);
            setupPawns(6, true);
            setupBackRank(0, false);
            setupPawns(1, false);
        } else {
            setupBackRank(7, false);
            setupPawns(6, false);
            setupBackRank(0, true);
            setupPawns(1, true);
        }
    }

    private void setupPawns(int row, boolean isWhite) {
        for (int col = 0; col < 8; col++)
            tiles[row][col].setPiece(new Pawn(isWhite, row, col));
    }

    private void setupBackRank(int row, boolean isWhite) {
        tiles[row][0].setPiece(new Rook(isWhite, row, 0));
        tiles[row][1].setPiece(new Knight(isWhite, row, 1));
        tiles[row][2].setPiece(new Bishop(isWhite, row, 2));
        tiles[row][3].setPiece(new Queen(isWhite, row, 3));
        tiles[row][4].setPiece(new King(isWhite, row, 4));
        tiles[row][5].setPiece(new Bishop(isWhite, row, 5));
        tiles[row][6].setPiece(new Knight(isWhite, row, 6));
        tiles[row][7].setPiece(new Rook(isWhite, row, 7));
    }

    public boolean move(int oldRow, int oldCol, int newRow, int newCol) {
        Piece piece = tiles[oldRow][oldCol].getPiece();
        if (piece == null) return false;

        if (piece.isWhite() != whiteTurn) return false;

        if (!piece.allMoves(tiles).contains(new Pair(newRow, newCol))) return false;

        Piece target = tiles[newRow][newCol].getPiece();

        tiles[newRow][newCol].setPiece(piece);
        tiles[oldRow][oldCol].setPiece(null);
        piece.setCol(newCol);
        piece.setRow(newRow);

        if (piece instanceof King king) {
            if (!king.hasMoved) king.hasMoved = true;

            // Castling detection — moved 2 squares horizontally
            if (Math.abs(newCol - oldCol) == 2) {
                int rookOldCol = (newCol > oldCol) ? 7 : 0;   // kingside → right rook, queenside → left rook
                int rookNewCol = (newCol > oldCol) ? newCol - 1 : newCol + 1; // rook moves next to king
                Tiles rookTile = tiles[newRow][rookOldCol];

                if (rookTile.getPiece() instanceof Rook rook) {
                    tiles[newRow][rookNewCol].setPiece(rook);  // move rook
                    tiles[newRow][rookOldCol].setPiece(null);
                    rook.setCol(rookNewCol);
                    rook.hasMoved = true;
                }
            }
        } else if (piece instanceof Rook rook) {
            if (!rook.hasMoved) rook.hasMoved = true;
        }

        if (piece instanceof Pawn pawn) {
            if (!pawn.hasMoved) pawn.hasMoved = true;

            // track double move
            pawn.hasMovedTwoSteps = Math.abs(newRow - oldRow) == 2;

            // En passant capture
            if (target == null && Math.abs(newRow - oldRow) == 1 && Math.abs(newCol - oldCol) == 1) {
                int captureRow = pawn.isWhite() ? newRow + 1 : newRow - 1;
                tiles[captureRow][newCol].setPiece(null);
            }

            if ((pawn.isWhite() && newRow == 0) || (!pawn.isWhite() && newRow == 7)) {
                String promoted = PromotionWindow.showDialog(ChessBoardUI.getFrame(), pawn.isWhite());
                if (promoted != null) {
                    switch (promoted) {
                        case "Queen" -> tiles[newRow][newCol].setPiece(new Queen(pawn.isWhite(), newRow, newCol));
                        case "Rook" -> tiles[newRow][newCol].setPiece(new Rook(pawn.isWhite(), newRow, newCol));
                        case "Bishop" -> tiles[newRow][newCol].setPiece(new Bishop(pawn.isWhite(), newRow, newCol));
                        case "Knight" -> tiles[newRow][newCol].setPiece(new Knight(pawn.isWhite(), newRow, newCol));
                    }
                }
            }
        }

        // ✅ Switch turn
        switchTurn();

        // ✅ Reset hasMovedTwoSteps for all pawns except the one that just moved
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = tiles[r][c].getPiece();
                if (p instanceof Pawn otherPawn && p != piece) {
                    otherPawn.hasMovedTwoSteps = false;
                }
            }
        }
        checkCondition();
        return true;
    }

    /** Check if a piece is pinned (static helper) */
    public static boolean pinned(Tiles[][] board, int oldRow, int oldCol, int newRow, int newCol){


        Piece piece = board[oldRow][oldCol].getPiece();
        if (piece == null) return false;

        Piece target = board[newRow][newCol].getPiece();

        board[newRow][newCol].setPiece(piece);
        board[oldRow][oldCol].setPiece(null);

        boolean isPinned = isKingInCheckStatic(board, piece.isWhite());

        board[oldRow][oldCol].setPiece(piece);
        board[newRow][newCol].setPiece(target);

        return isPinned;
    }

    private static boolean isKingInCheckStatic(Tiles[][] board, boolean whiteKing) {
        int kingRow = -1, kingCol = -1;

        // 1️⃣ Locate the king
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board[r][c].getPiece();
                if (p != null && p.isWhite() == whiteKing && p.getName().equals("King")) {
                    kingRow = r;
                    kingCol = c;
                    break;
                }
            }
        }

        if (kingRow == -1) return false; // king not found (shouldn't happen)

        // 2️⃣ Directions for rooks/queens (straight lines)
        int[][] rookDirs = { {-1,0}, {1,0}, {0,-1}, {0,1} };
        for (int[] dir : rookDirs) {
            int r = kingRow + dir[0];
            int c = kingCol + dir[1];
            while (r >= 0 && r < 8 && c >= 0 && c < 8) {
                Piece p = board[r][c].getPiece();
                if (p != null) {
                    if (p.isWhite() != whiteKing &&
                            (p.getName().equals("Rook") || p.getName().equals("Queen")))
                        return true;
                    break; // blocked
                }
                r += dir[0];
                c += dir[1];
            }
        }

        // 3️⃣ Directions for bishops/queens (diagonals)
        int[][] bishopDirs = { {-1,-1}, {-1,1}, {1,-1}, {1,1} };
        for (int[] dir : bishopDirs) {
            int r = kingRow + dir[0];
            int c = kingCol + dir[1];
            while (r >= 0 && r < 8 && c >= 0 && c < 8) {
                Piece p = board[r][c].getPiece();
                if (p != null) {
                    if (p.isWhite() != whiteKing &&
                            (p.getName().equals("Bishop") || p.getName().equals("Queen")))
                        return true;
                    break;
                }
                r += dir[0];
                c += dir[1];
            }
        }

        // 4️⃣ Knight checks
        int[][] knightMoves = {
                {-2,-1}, {-2,1}, {-1,-2}, {-1,2},
                {1,-2}, {1,2}, {2,-1}, {2,1}
        };
        for (int[] move : knightMoves) {
            int r = kingRow + move[0];
            int c = kingCol + move[1];
            if (r >= 0 && r < 8 && c >= 0 && c < 8) {
                Piece p = board[r][c].getPiece();
                if (p != null && p.isWhite() != whiteKing && p.getName().equals("Knight"))
                    return true;
            }
        }

        // 5️⃣ Pawn attacks (direction depends on color)
        int pawnDir = whiteKing ? -1 : 1; // white pawns move up, black down
        int[][] pawnAttacks = { {pawnDir, -1}, {pawnDir, 1} };
        for (int[] att : pawnAttacks) {
            int r = kingRow + att[0];
            int c = kingCol + att[1];
            if (r >= 0 && r < 8 && c >= 0 && c < 8) {
                Piece p = board[r][c].getPiece();
                if (p != null && p.isWhite() != whiteKing && p.getName().equals("Pawn"))
                    return true;
            }
        }

        // 6️⃣ Opposing king (adjacent squares)
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;
                int r = kingRow + dr;
                int c = kingCol + dc;
                if (r >= 0 && r < 8 && c >= 0 && c < 8) {
                    Piece p = board[r][c].getPiece();
                    if (p != null && p.isWhite() != whiteKing && p.getName().equals("King"))
                        return true;
                }
            }
        }

        return false; // ✅ no threats found
    }

    private void checkCondition() {
        Piece whiteKing = null;
        Piece blackKing = null;

        // Find kings
        for (Tiles[] row : tiles) {
            for (Tiles tile : row) {
                Piece p = tile.getPiece();
                if (p != null && p.getName().equalsIgnoreCase("King")) {
                    if (p.isWhite()) whiteKing = p;
                    else blackKing = p;
                }
            }
        }

        if (whiteKing == null || blackKing == null) {
            System.err.println("Error: One or both kings missing!");
            return;
        }

        boolean currentPlayerIsWhite = whiteTurn;
        boolean kingInCheck = isKingInCheckStatic(tiles, currentPlayerIsWhite);

        // Check if current player has ANY legal move
        boolean hasLegalMove = false;
        outer:
        for (Tiles[] row : tiles) {
            for (Tiles tile : row) {
                Piece p = tile.getPiece();
                if (p != null && p.isWhite() == currentPlayerIsWhite) {
                    for (Pair move : p.allMoves(tiles)) {
                        int oldRow = p.getRow();
                        int oldCol = p.getCol();
                        int newRow = move.getRow();
                        int newCol = move.getCol();

                        if (!pinned(tiles, oldRow, oldCol, newRow, newCol)) {
                            hasLegalMove = true;
                            break outer;
                        }
                    }
                }
            }
        }

        if (!hasLegalMove) {
            if (kingInCheck) {
                condition = currentPlayerIsWhite ? "Checkmate_Black" : "Checkmate_White";
            } else {
                condition = "Stalemate";
            }
        } else {
            condition = "";
        }

        System.out.println("Condition: " + condition);
    }
}
