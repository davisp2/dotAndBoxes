package com.example.dotandboxes;

import java.util.Arrays;

public class GameBoard {
    private int size;
    private boolean[][] horizontalLines;
    private boolean[][] verticalLines;
    private int[][] cellOwnership;

    GameBoard(final int setSize) {
        if (setSize <= 0) {
            throw new IllegalArgumentException();
        }
        size = setSize;
        horizontalLines = new boolean[size][size + 1];
        verticalLines = new boolean[size + 1][size];
        cellOwnership = new int[size][size];
        cellOwnership[1][1] = 1;
        cellOwnership[1][2] = 2;
    }

    public void placeLine(int x, int y, int player, String lineType) {
        if (lineType.equals("horizontal")) {
            horizontalLines[x][y] = true;
            checkCellCaptured(x, y, player);
            checkCellCaptured(x, y - 1, player);
        } else if (lineType.equals("vertical")) {
            verticalLines[x][y] = true;
            checkCellCaptured(x, y, player);
            checkCellCaptured(x - 1, y, player);
        }
    }

    private void checkCellCaptured(int x, int y, int player) {
        if (x >= 0 && x < size && y >= 0 && y < size) {
            if (horizontalLines[x][y] && horizontalLines[x][y + 1]
                    && verticalLines[x][y] && verticalLines[x + 1][y]) {
                cellOwnership[x][y] = player;
            }
        }
    }

    public int getSize() {
        return size;
    }

    public boolean[][] getHorizontalLines() {
        return horizontalLines;
    }

    public boolean[][] getVerticalLines() {
        return verticalLines;
    }

    public int[][] getCellOwnership() {
        return cellOwnership;
    }
}