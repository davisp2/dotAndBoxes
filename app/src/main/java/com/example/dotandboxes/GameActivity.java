package com.example.dotandboxes;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

public class GameActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);
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
        builder.setMessage(R.string.end_conf);
        builder.setNegativeButton(R.string.cancel, null);
        builder.setPositiveButton(R.string.end, (unused1, unused2) -> {
            //add log out of room feature
            finish();}
            );
        builder.create().show();
    }
}
