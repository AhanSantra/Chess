package com.github.ahansantra.chess.pieces;

import com.github.ahansantra.chess.board.Tiles;
import com.github.ahansantra.chess.utils.Pair;

import java.util.ArrayList;

abstract public class Piece {
    String name;
    boolean isWhite;
    int row;
    int col;

    Piece (int row , int col, boolean isWhite, String Name){
        this.row = row;
        this.col = col;
        this.isWhite = isWhite;
        this.name = Name;
    }
    public abstract ArrayList<Pair> allMoves(Tiles[][] board);

    // Getters
    public String getName() {return name;}
    public boolean isWhite() {return isWhite;}
    public int getRow() {return row;}
    public int getCol() {return col;}

    // Setters
    public void setRow(int row) {this.row = row;}
    public void setCol(int col) {this.col = col;}
}
