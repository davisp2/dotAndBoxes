package com.example.dotandboxes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MenuActivity extends AppCompatActivity {

    FirebaseDatabase database;
    String playerName;
    DatabaseReference lobbyRef;
    DatabaseReference newRoomRef;
    Button createGame;
    Map<String, Long> lobbyList;
    private int value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        database = FirebaseDatabase.getInstance();
        SharedPreferences pref = getSharedPreferences("PREFS", 0);
        playerName = pref.getString("username", "");

        TextView t =findViewById(R.id.nameDisplay);
        t.setText(getResources().getString(R.string.display_user, playerName));

        //this part will handle creating the new game
        createGame = findViewById(R.id.createGame);
        createGame.setOnClickListener(unused -> {
            createGame.setEnabled(false);
            createGame();
        });
        database.getReference("rooms/" + playerName).child("size").setValue(3);
        //handles connection in multiplayer
        findViewById(R.id.retryConnectButton).setOnClickListener(unused -> connect());
        connect();

        lobbyRef = database.getReference("rooms");
        lobbyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                connect();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //error
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        connect();
    }


    private void createGame() {


        LinearLayout createLayout = findViewById(R.id.createGameBlock);
        createLayout.removeAllViews();
        createLayout.setVisibility(View.VISIBLE);

        View chunk = getLayoutInflater().inflate(R.layout.chunk_create_game,
                createLayout, false);
        createLayout.addView(chunk);
        TextView ownerLabel =findViewById(R.id.create_game_owner);
        ownerLabel.setText(getResources().getString(R.string.game_owner, playerName));
        newRoomRef = database.getReference("rooms/" + playerName);
        newRoomRef.child("size").setValue(3);
        SeekBar bar = findViewById(R.id.seekBar);
        TextView sizeDisplay = findViewById(R.id.sizeDisplay);
        sizeDisplay.setText(getResources().getString(R.string.game_size, 3, 3));

        Intent intent = new Intent(getApplicationContext(), GameActivity.class);
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                value = 3 + (progress);
                sizeDisplay.setText(getResources().getString(R.string.game_size, value, value));
                newRoomRef.child("size").setValue(value);
                SharedPreferences pref = getSharedPreferences("PREFS", 0);
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt("size", value);
                editor.apply();
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
        });
        Button create = findViewById(R.id.create);
        create.setOnClickListener(unused -> {
            SharedPreferences pref = getSharedPreferences("PREFS", 0);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("room", playerName);
            editor.putString("playerID", "player1");
            editor.putBoolean("started", false);
            editor.apply();
            newRoomRef.child("player1").child("player").setValue(playerName);
            System.out.println(pref.getBoolean("start", false) + "at launch");
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        database.getReference("player").child(playerName).removeValue();
    }

    private void connect() {
        findViewById(R.id.OpenLobbyGroup).setVisibility(View.GONE);
        findViewById(R.id.loadGroup).setVisibility(View.GONE);
        getLobbies();
    }

    private void getLobbies() {
        lobbyList = new HashMap<>();
        lobbyRef = database.getReference("rooms");
        lobbyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> rooms = dataSnapshot.getChildren();
                for(DataSnapshot snapshot: rooms) {
                    if (snapshot.child("player2").child("player").getValue() == null
                        && snapshot.child("player1").child("player").getValue() != null) {
                        Long size = (Long) snapshot.child("size").getValue();
                        lobbyList.put(snapshot.child("player1").child("player").getValue().toString(), size);
                    }
                }
                setUpUi(lobbyList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //error
            }
        });
    }


    private void setUpUi(Map<String, Long> lobby) {
        findViewById(R.id.OpenLobbyGroup).setVisibility(View.VISIBLE);

        LinearLayout openLayout = findViewById(R.id.openGamesList);
        openLayout.removeAllViews();
        openLayout.setVisibility(View.VISIBLE);
        for (Map.Entry<String, Long> room : lobby.entrySet()) {
            View chunk = getLayoutInflater().inflate(R.layout.chunk_open_lobby,
                    openLayout, false);
            openLayout.addView(chunk);
            Button join = chunk.findViewById(R.id.join_game);
            if (room.getKey().equals(playerName)) {
                join.setEnabled(false);
            }
            join.setOnClickListener(v -> {
                lobbyRef = database.getReference("rooms");
                lobbyRef.child(room.getKey()).child("player2").child("player").setValue(playerName);
                SharedPreferences pref = getSharedPreferences("PREFS", 0);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("playerID", "player2");
                editor.putString("room", room.getKey());
                editor.putInt("size", room.getValue().intValue());
                editor.apply();
                startActivity(new Intent(this, GameActivity.class));
            });
            TextView ownerLabel = chunk.findViewById(R.id.game_owner);
            ownerLabel.setText(getResources().getString(R.string.game_owner, room.getKey()));
            TextView infoLabel = chunk.findViewById(R.id.game_size); // Shows both the user's role and the game mode
            infoLabel.setText(getResources().getString(R.string.game_size, room.getValue(), room.getValue()));
        }
    }

}
