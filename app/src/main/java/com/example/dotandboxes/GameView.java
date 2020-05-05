package com.example.dotandboxes;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GameView extends View {
    private float lineLength;
    private Paint paint;
    private GameBoard board;
    private double boardX;
    private double boardY;
    private double boardHeight;
    private double boardWidth;
    private int player;
    private DatabaseReference roomRef;
    private FirebaseDatabase db;
    private String playerID;

    private void init() {
        SharedPreferences pref = getContext().getSharedPreferences("PREFS", 0);
        int size = pref.getInt("size", 3);
        playerID = pref.getString("playerID", "");
        String roomID = pref.getString("room", "");
        db = FirebaseDatabase.getInstance();
        roomRef = db.getReference("rooms/" + roomID);
        if (playerID.equals("player1")) {
            player = 1;
        } else {
            player = 2;
        }
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
        //System.out.println(getWidth() + " : " + size + " : " + lineLength + " : " + lineThickness);
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
        boolean started = getContext().getSharedPreferences("PREFS",0).getBoolean("start", false);
        System.out.println(started);
        if (event.getAction() == MotionEvent.ACTION_UP && started) {
            float x = event.getX();
            float y = event.getY();
            if (x >= boardX && x <= boardX + boardWidth && y >= boardY && y <= boardY + boardHeight) {
                x -= boardX;
                y -= boardY;

                float disX = x % lineLength;
                float disY = y % lineLength;
                int cellX = (int) (x / lineLength);
                int cellY = (int) (y / lineLength);
                String direction;
                if (disX + disY > lineLength) {
                    if (disX > disY) {
                        board.placeLine(cellX + 1, cellY, player, "vertical");
                        direction = "vertical";
                        System.out.println("draw" + cellX + 1 + "" + cellY + "for" + player + direction);
                        roomRef.child(playerID).child("x").setValue(cellX + 1);
                        roomRef.child(playerID).child("y").setValue(cellY);
                    } else {
                        board.placeLine(cellX, cellY + 1, player, "horizontal");
                        direction = "horizontal";
                        roomRef.child(playerID).child("x").setValue(cellX);
                        roomRef.child(playerID).child("y").setValue(cellY + 1);
                        System.out.println("draw" + cellX + "" + cellY + 1 + "for" + player + direction);
                    }
                } else {
                    if (disX > disY) {
                        board.placeLine(cellX, cellY, player, "horizontal");
                        direction = "horizontal";
                        System.out.println("draw" + cellX + "" + cellY + "for" + player + direction);
                    } else {
                        board.placeLine(cellX, cellY, player, "vertical");
                        direction = "vertical";
                        System.out.println("draw" + cellX + "" + cellY + "for" + player + direction);
                    }
                    roomRef.child(playerID).child("x").setValue(cellX);
                    roomRef.child(playerID).child("y").setValue(cellY);
                }
                roomRef.child(playerID).child("direction").setValue(direction);
            }
            invalidate();
        }
        return true;
    }

    private void drawNew(boolean disc, float x, float y, int p) {

    }

    @Override
    public void onDraw(Canvas canvas) {
        boolean started = getContext().getSharedPreferences("PREFS",0).getBoolean("start", false);
        if (started) {
            SharedPreferences pack = getContext().getSharedPreferences("UPDATES", 0);
            board.placeLine(pack.getInt("x", -1), pack.getInt("y", -1),
                    (player + 1)%2, pack.getString("direction", ""));
        }

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
