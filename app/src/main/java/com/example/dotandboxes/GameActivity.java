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

        //this to detect if game owner leaves the game
        database.getReference("rooms").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getKey().equals(roomID)) {
                    finish();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        database.getReference("rooms").child(roomID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    if (playerID.equals("player1")) {
                        Object player2 = dataSnapshot.child("player2").getValue();
                        if (player2 != null && !player2.toString().equals("")) {
                            otherPlayer = player2.toString();
                        }

                    } else {
                        otherPlayer = dataSnapshot.child("player1").getValue().toString();
                    }
                    System.out.println(otherPlayer);
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        playerID = pref.getString("playerID", "");


        setContentView(R.layout.activity_game);

        TextView playerColor = findViewById(R.id.textView);
        if (playerID.equals("player1")) {
            playerColor.setText(getResources().getString(R.string.player, "RED", otherPlayer));
        } else {
            playerColor.setText(getResources().getString(R.string.player, "BLUE", otherPlayer));
        }

        String otherP;
        if (playerID.equals("player1")) {
            otherP = "player2";
        } else {
            otherP = "player1";
        }
        System.out.println(otherP + "xxxxxxxxxxxxxxxxxxxxxxx");
        roomRef.child(otherP).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    String direction = (String) dataSnapshot.child("direction").getValue();
                    System.out.println(direction);
                    long x = (Long) dataSnapshot.child("x").getValue();
                    long y = (Long) dataSnapshot.child("y").getValue();
                    System.out.println(x + "" + y);
                    packageInfo(x, y, otherPlayer, direction);
                    findViewById(R.id.gameView).invalidate();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    String direction = (String) dataSnapshot.child("direction").getValue();
                    System.out.println(direction);
                    long x = (Long) dataSnapshot.child("x").getValue();
                    long y = (Long) dataSnapshot.child("y").getValue();
                    System.out.println(x + "" + y);
                    packageInfo(x, y, otherPlayer, direction);
                    findViewById(R.id.gameView).invalidate();
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
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
                roomRef.removeValue();
            }
            finish();}
            );
        builder.create().show();
    }
}
