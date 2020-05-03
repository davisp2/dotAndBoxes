package com.example.dotandboxes;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class GameView extends View {
    private int lineLength;
    private int lineThickness;
    private Paint paint;
    private GameBoard board;

    private void init() {
        int size = 8;
        paint = new Paint();
        board = new GameBoard(size);
        lineLength = (int) Math.ceil((double) Resources.getSystem().getDisplayMetrics().widthPixels * .75
                / (double) size);
        lineThickness = (int) Math.ceil((double) lineLength * .1);
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
    public void onDraw(Canvas canvas) {
        //declaring variables
        int s = board.getSize();
        int xOrigin = getWidth() / 2 - (s * lineLength) / 2;
        int yOrigin = getHeight() / 2 - (s * lineLength) / 2;

        //DRAWING BOARD
        //drawing colored cells first
        int[][] cO = board.getCellOwnership();
        for (int i = 0; i < s; i++) {
            for (int j = 0; j < s; j++) {
                if (cO[i][j] == 1) {
                    paint.setColor(PlayerID.ONE_COLOR);
                    canvas.drawRect((float) i * lineLength + xOrigin,
                            (float) j * lineLength + yOrigin,
                            (float) (i + 1) * lineLength + xOrigin,
                            (float) (j + 1) * lineLength + yOrigin, paint);
                } else if (cO[i][j] == 2) {
                    paint.setColor(PlayerID.TWO_COLOR);
                    canvas.drawRect((float) i * lineLength + xOrigin,
                            (float) j * lineLength + yOrigin,
                            (float) (i + 1) * lineLength + xOrigin,
                            (float) (j + 1) * lineLength + yOrigin, paint);
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
    }
}
