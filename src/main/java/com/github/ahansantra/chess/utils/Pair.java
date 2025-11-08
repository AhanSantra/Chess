package com.github.ahansantra.chess.utils;
import java.util.Objects;

public class Pair {
    private int row;
    private int col;

    public Pair(int row, int col) {
        this.row = row;
        this.col = col;
    }

    // Getters
    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    // Setters
    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    // equals() method
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair pair)) return false;
        return row == pair.row && col == pair.col;
    }

    // hashCode() method
    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    // toString() method
    @Override
    public String toString() {
        return "Pair{" +
                "row=" + row +
                ", col=" + col +
                '}';
    }
}
