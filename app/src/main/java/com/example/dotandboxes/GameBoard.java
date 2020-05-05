package com.example.dotandboxes;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GameBoard {
    private int size;
    private boolean[][] horizontalLines;
    private boolean[][] verticalLines;
    private int[][] cellOwnership;
    private int[] score;
    private int[] turn;

    GameBoard(final int setSize) {
        if (setSize <= 0) {
            throw new IllegalArgumentException();
        }
        size = setSize;
        horizontalLines = new boolean[size][size + 1];
        verticalLines = new boolean[size + 1][size];
        cellOwnership = new int[size][size];
        score = new int[2];
        turn = new int[]{-99, 1, 0};
    }

    public boolean placeLine(int x, int y, int player, String lineType) {
        if (x >= 0 && y >= 0 && turn[player] == 1) {
            if (lineType.equals("horizontal") && x < horizontalLines.length && y < horizontalLines[0].length) {
                if (!horizontalLines[x][y]) {
                    horizontalLines[x][y] = true;
                    checkCellCaptured(x, y, player);
                    checkCellCaptured(x, y - 1, player);
                }
            } else if (lineType.equals("vertical") && x < verticalLines.length && y < verticalLines[0].length ) {
                if (!verticalLines[x][y]) {
                    verticalLines[x][y] = true;
                    checkCellCaptured(x, y, player);
                    checkCellCaptured(x - 1, y, player);
                }
            }
            System.out.println("drawn" + lineType + "" + x + "" + y + "player" + player);
            if (player == 1) {
                turn[1] = 0;
                turn[2] = 1;
            } else {
                turn[1] =1 ;
                turn[2] = 0;
            }
            return true;
        }
        return false;
    }

    private void checkCellCaptured(int x, int y, int player) {
        if (x >= 0 && x < size && y >= 0 && y < size) {
            if (horizontalLines[x][y] && horizontalLines[x][y + 1]
                    && verticalLines[x][y] && verticalLines[x + 1][y]) {
                cellOwnership[x][y] = player;
                score[player - 1]++;
            }
        }
    }

    public int[] getScore() {
        return score;
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
