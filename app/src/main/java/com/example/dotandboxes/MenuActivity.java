package com.example.dotandboxes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        database = FirebaseDatabase.getInstance();
        SharedPreferences pref = getSharedPreferences("PREFS", 0);
        playerName = pref.getString("playerName", "");
        TextView t =findViewById(R.id.nameDisplay);
        t.setText(getResources().getString(R.string.display_user, playerName));

        //this part will handle creating the new game
        findViewById(R.id.createGame).setOnClickListener(unused -> createGame());

        //handles connection in multiplayer
        findViewById(R.id.retryConnectButton).setOnClickListener(unused -> connect());
        connect();
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
        newRoomRef.child("player1").setValue(playerName);

        SeekBar bar = findViewById(R.id.seekBar);
        TextView sizeDisplay = findViewById(R.id.sizeDisplay);
        sizeDisplay.setText(getResources().getString(R.string.game_size, 3, 3));

        Intent intent = new Intent(getApplicationContext(), GameActivity.class);
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int value = 3 + (progress);
                sizeDisplay.setText(getResources().getString(R.string.game_size, value, value));
                newRoomRef.child("size").setValue(value);
                intent.putExtra("roomSize", value);
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
        });
        intent.putExtra("roomName", playerName);
        startActivity(intent);
    }


    private void connect() {
        findViewById(R.id.OpenLobbyGroup).setVisibility(View.GONE);
        findViewById(R.id.loadGroup).setVisibility(View.GONE);

        //more implementation when we connect to firebase
        Map<String, Integer> openLobby = new HashMap<>();
        openLobby.put("placeHolder", 3);
        openLobby.put("test", 10);
        openLobby.put("subject", 30);
        setUpUi(openLobby);
    }

    //not yet put to use 
    private Map<String, Integer> getLobbies() {
        Map<String, Integer> lobby = new HashMap<>();
        lobbyRef = database.getReference("rooms");
        lobbyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> rooms = dataSnapshot.getChildren();
                for(DataSnapshot snapshot: rooms) {
                    lobby.put((String) snapshot.child("player1").getValue(),
                            (Integer) snapshot.child("roomSize").getValue());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //error
            }

        });
        return lobby;
    }


    private void setUpUi(Map<String, Integer> lobby) {
        //these are placeholder values that will be changed
        findViewById(R.id.OpenLobbyGroup).setVisibility(View.VISIBLE);

        LinearLayout openLayout = findViewById(R.id.openGamesList);
        openLayout.removeAllViews();
        openLayout.setVisibility(View.VISIBLE);
        for (Map.Entry<String, Integer> room : lobby.entrySet()) {
            View chunk = getLayoutInflater().inflate(R.layout.chunk_open_lobby,
                    openLayout, false);
            openLayout.addView(chunk);
            Button join = chunk.findViewById(R.id.join_game);
            join.setOnClickListener(v -> {
                startActivity(new Intent(this, GameActivity.class));
            });
            TextView ownerLabel = chunk.findViewById(R.id.game_owner);
            ownerLabel.setText(getResources().getString(R.string.game_owner, room.getKey()));
            TextView infoLabel = chunk.findViewById(R.id.game_size); // Shows both the user's role and the game mode
            infoLabel.setText(getResources().getString(R.string.game_size, room.getValue(), room.getValue()));
        }
    }

}
