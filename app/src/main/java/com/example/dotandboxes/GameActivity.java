package com.example.dotandboxes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GameActivity extends AppCompatActivity {

    FirebaseDatabase database;
    String playerName;
    DatabaseReference roomRef;
    String roomID;
    String playerID;
    String otherPlayer = "none";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
        //finds the local player's username
        SharedPreferences pref = getSharedPreferences("PREFS", 0);
        playerName = pref.getString("username", "");
        roomID = pref.getString("room", "");
        roomRef = database.getReference("rooms/" + roomID);
        playerID = pref.getString("playerID", "");
        int other;
        if (playerID.equals("player1")) {
            other = 2;
        } else {
            other = 1;
        }
        setContentView(R.layout.activity_game);
        TextView playerColor = findViewById(R.id.textView);
        roomRef.child("player" + other).child("player").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot data) {
                System.out.println(data.exists());
                if (data.exists()) {
                    otherPlayer = data.getValue().toString();
                    if (playerID.equals("player1")) {
                        playerColor.setText(getResources().getString(R.string.player, "RED", otherPlayer));
                    } else {
                        playerColor.setText(getResources().getString(R.string.player, "BLUE", otherPlayer));
                    }
                    System.out.println(otherPlayer);
                    SharedPreferences pref = getSharedPreferences("start", 0);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("start", true);
                    editor.apply();
                } else {
                    if (playerID.equals("player1")) {
                        playerColor.setText(getResources().getString(R.string.player, "RED", "NONE"));
                    } else {
                        playerColor.setText(getResources().getString(R.string.player, "BLUE", "NONE"));
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        roomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data) {
                if (!data.exists()){
                    finish();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        roomRef.child("player" + other).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot data) {
                if (data.child("x").exists() && data.child("y").exists()) {
                    double x = (double) data.child("x").getValue();
                    double y = (double) data.child("y").getValue();
                    System.out.println(x + " and " + y);
                    packageInfo(x,y, other);
                    findViewById(R.id.gameView).postInvalidate();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });






        //the exit game button
        Button exit = findViewById(R.id.exit);
        exit.setOnClickListener(v -> {
            endGame();
        });
    }


    private void packageInfo(double x, double y, int p) {
        SharedPreferences pref = getSharedPreferences("UPDATES", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("player", p);
        editor.putFloat("x",(float) x);
        editor.putFloat("y", (float) y);
        editor.apply();
        System.out.println(pref.getFloat("x", -1.2f));
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    private void endGame() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (playerID.equals("player1")) {
            builder.setMessage(R.string.end_conf2);
        } else {
            builder.setMessage(R.string.end_conf);
        }
        builder.setNegativeButton(R.string.cancel, null);
        builder.setPositiveButton(R.string.end, (unused1, unused2) -> {
            roomRef.child(playerID).removeValue();
            if (playerID.equals("player1")) {
                roomRef.child("host left").setValue("true");
                roomRef.removeValue();
            }
            finish();}
            );
        builder.create().show();
    }
}
