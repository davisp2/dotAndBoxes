package com.example.dotandboxes;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class GameView extends View {
    private float lineLength;
    private Paint paint;
    private GameBoard board;
    private double boardX;
    private double boardY;
    private double boardHeight;
    private double boardWidth;

    private void init() {
        //note: this size is at a temporary value. If we have time, we can add an option
        //for the user to change the size.
        int size = 3;
        float textSize = 100;
        paint = new Paint();
        paint.setTextSize(textSize);
        paint.setTextAlign(Align.CENTER);
        board = new GameBoard(size);
        double viewWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        double viewHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        lineLength = (int) Math.ceil(viewWidth * .75
                / (double) size);
        int lineThickness = (int) Math.ceil((double) lineLength * .1);
        boardWidth = lineLength * board.getSize();
        boardHeight = boardWidth;
        boardX = viewWidth * .125;
        boardY = viewHeight/2 - (boardHeight) / 2;
        System.out.println(getWidth() + " : " + size + " : " + lineLength + " : " + lineThickness);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(lineThickness);
    }

    public GameView(Context context) {
        super(context);
        init();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            float x = event.getX();
            float y = event.getY();

            if (x >= boardX && x <= boardX + boardWidth && y >= boardY && y <= boardY + boardHeight) {
                x -= boardX;
                y -= boardY;

                float disX = x % lineLength;
                float disY = y % lineLength;
                int cellX = (int) (x / lineLength);
                int cellY = (int) (y / lineLength);
                if (disX + disY > lineLength) {
                    if (disX > disY) {
                        board.placeLine(cellX + 1, cellY, 1, "vertical");
                    } else {
                        board.placeLine(cellX, cellY + 1, 1, "horizontal");
                    }
                } else {
                    if (disX > disY) {
                        board.placeLine(cellX , cellY, 1, "horizontal");
                    } else {
                        board.placeLine(cellX, cellY, 1, "vertical");
                    }
                }
                invalidate();
            }
        }
        return true;
    }

    @Override
    public void onDraw(Canvas canvas) {
        //declaring variables
        int s = board.getSize();
        float xOrigin = (float) boardX;
        float yOrigin = (float) boardY;

        //DRAWING BOARD
        //drawing colored cells first
        int[][] cO = board.getCellOwnership();
        for (int i = 0; i < s; i++) {
            for (int j = 0; j < s; j++) {
                if (cO[i][j] == 1) {
                    paint.setColor(PlayerID.ONE_COLOR);
                    canvas.drawRect( i * lineLength + xOrigin,
                             j * lineLength + yOrigin,
                             (i + 1) * lineLength + xOrigin,
                             (j + 1) * lineLength + yOrigin, paint);
                } else if (cO[i][j] == 2) {
                    paint.setColor(PlayerID.TWO_COLOR);
                    canvas.drawRect( i * lineLength + xOrigin,
                             j * lineLength + yOrigin,
                             (i + 1) * lineLength + xOrigin,
                             (j + 1) * lineLength + yOrigin, paint);
                }
            }
        }

        //drawing horizontal lines
        boolean[][] hL = board.getHorizontalLines();
        for (int i = 0; i < s; i++) {
            for (int j = 0; j < s + 1; j++) {
                if (hL[i][j]) {
                    paint.setColor(Color.BLACK);
                } else {
                    paint.setColor(Color.LTGRAY);
                }
                canvas.drawLine(i * lineLength + xOrigin, j * lineLength + yOrigin,
                        (i + 1) * lineLength + xOrigin, j * lineLength + yOrigin, paint);
            }
        }

        //drawing vertical lines
        boolean[][] vL = board.getVerticalLines();
        for (int i = 0; i < s + 1; i++) {
            for (int j = 0; j < s; j++) {
                if (vL[i][j]) {
                    paint.setColor(Color.BLACK);
                } else {
                    paint.setColor(Color.LTGRAY);
                }
                canvas.drawLine(i * lineLength + xOrigin, j * lineLength + yOrigin,
                        i * lineLength + xOrigin, (j + 1) * lineLength + yOrigin, paint);
            }
        }

        //drawing dots on each vertex
        paint.setColor(Color.BLACK);
        for (int i = 0; i < s + 1; i++) {
            for (int j = 0;  j < s + 1; j++) {
                canvas.drawPoint(i * lineLength  + xOrigin, j * lineLength  + yOrigin, paint);
            }
        }

        //drawing Text score
        int[] score = board.getScore();

        if (score[0] + score[1] < s * s) {
            canvas.drawText("Red: " + score[0] + " Blue: " + score[1], (float) (boardX + boardWidth / 2),
                    (float) (boardY - boardHeight / 4), paint);
        } else {
            //display winner
            if (score[0] > score[1]){
                canvas.drawText("Red Wins!", (float) (boardX + boardWidth / 2),
                    (float) (boardY - boardHeight / 4), paint);
            } else if (score[0] < score[1]) {
                canvas.drawText("Blue Wins!", (float) (boardX + boardWidth / 2),
                        (float) (boardY - boardHeight / 4), paint);
            } else {
                canvas.drawText("It's a Tie!", (float) (boardX + boardWidth / 2),
                        (float) (boardY - boardHeight / 4), paint);
            }
        }
    }
}
