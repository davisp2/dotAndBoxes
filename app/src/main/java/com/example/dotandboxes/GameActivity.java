package com.example.dotandboxes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
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
        playerID = pref.getString("playerID", "");


        setContentView(R.layout.activity_game);
        TextView playerColor = findViewById(R.id.textView);
        if (playerID.equals("player1")) {
            playerColor.setText(getResources().getString(R.string.player, "RED"));
        } else {
            playerColor.setText(getResources().getString(R.string.player, "Blue"));
        }
        //the exit game button
        Button exit = findViewById(R.id.exit);
        exit.setOnClickListener(v -> {
            endGame();
        });
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
