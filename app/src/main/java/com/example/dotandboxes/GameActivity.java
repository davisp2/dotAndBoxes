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
        String other;
        if (playerID.equals("player1")) {
            other = "player2";
        } else {
            other = "player1";
        }
        setContentView(R.layout.activity_game);
        TextView playerColor = findViewById(R.id.textView);

        roomRef.child(other).child("player").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot data) {
                if (data.exists()) {
                    otherPlayer = data.getValue().toString();
                    if (playerID.equals("player1")) {
                        playerColor.setText(getResources().getString(R.string.player, "RED", otherPlayer));
                    } else {
                        playerColor.setText(getResources().getString(R.string.player, "BLUE", otherPlayer));
                    }
                    SharedPreferences pref = getSharedPreferences("start", 0);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("start", true);
                    editor.apply();
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

        roomRef.child(other).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot data) {
                if (data.child("x").exists() && data.child("y").exists() && data.child("direction").exists()) {
                    String direction = data.child("direction").getValue().toString();
                    long x = (long) data.child("x").getValue();
                    long y = (long) data.child("y").getValue();
                    System.out.println(data.getValue());
                    packageInfo(x,y, other, direction);
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

    private void packageInfo(long x, long y, String p, String d) {
        SharedPreferences pref = getSharedPreferences("UPDATE", 0);
        boolean send = pref.getBoolean("start", false);
        if (send) {
            SharedPreferences.Editor editor = pref.edit();
            if (p.equals("player1")) {
                editor.putInt("player", 1);
            } else {
                editor.putInt("player", 2);
            }
            editor.putInt("x", (int) x);
            editor.putInt("y", (int) y);
            editor.putString("direction", d);
            editor.apply();
        }
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
