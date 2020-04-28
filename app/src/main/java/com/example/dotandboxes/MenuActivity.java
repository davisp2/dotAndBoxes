package com.example.dotandboxes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MenuActivity extends AppCompatActivity {

    //just a dummy variable to hold places for now
    private boolean placeHolding = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //this part will handle creating the new game

        findViewById(R.id.createGame).setOnClickListener(unused -> startActivity(
                new Intent(this, GameActivity.class)));

        //handles connection in multiplayer
        findViewById(R.id.retryConnectButton).setOnClickListener(unused -> connect());
        connect();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        connect();
    }

    private void connect() {
        findViewById(R.id.OpenLobbyGroup).setVisibility(View.GONE);
        findViewById(R.id.loadGroup).setVisibility(View.GONE);
        //more implementation when we connect to firebase
        if(!placeHolding) {
            setUpUi();
        }
    }
    private void setUpUi() {
        //these are placeholder values that will be changed
        String gameOwner = "placeHolder";
        int[] boardSize = new int[] {2, 2};

        findViewById(R.id.OpenLobbyGroup).setVisibility(View.VISIBLE);

        LinearLayout openLayout = findViewById(R.id.openGamesList);
        openLayout.setVisibility(View.VISIBLE);
        View chunk = getLayoutInflater().inflate(R.layout.chunk_open_lobby,
                openLayout, false);
        openLayout.addView(chunk);
        Button join = chunk.findViewById(R.id.join_game);
        join.setOnClickListener(v -> {
            startActivity(new Intent(this, GameActivity.class));
        });
        TextView ownerLabel = chunk.findViewById(R.id.game_owner);
        ownerLabel.setText(getResources().getString(R.string.game_owner, gameOwner));
        TextView infoLabel = chunk.findViewById(R.id.game_size); // Shows both the user's role and the game mode
        infoLabel.setText(getResources().getString(R.string.game_size, boardSize[0], boardSize[1]));
        placeHolding = true;
    }

}
