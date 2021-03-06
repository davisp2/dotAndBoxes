package com.example.dotandboxes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference playerRef;
    Button login;
    EditText nameInput;
    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        database = FirebaseDatabase.getInstance();

        //checks if the player already exists
        SharedPreferences preferences = getSharedPreferences("PREFS", 0);
        SharedPreferences.Editor edit = preferences.edit();
        edit.clear();
        edit.apply();

        SharedPreferences update = getSharedPreferences("UPDATE CELL", 0);
        SharedPreferences.Editor editg = update.edit();
        editg.clear();
        editg.apply();

        login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameInput = findViewById(R.id.enterName);
                userName = nameInput.getText().toString();
                nameInput.setText("");
                if(!userName.equals("")) {
                    login.setText(R.string.loggingin);
                    login.setEnabled(false);
                    playerRef = database.getReference("player/" + userName);
                    doLogin();
                    playerRef.setValue("");
                }
            }
        });

    }
    public void doLogin() {
        playerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!userName.equals("")) {
                    SharedPreferences pref = getSharedPreferences("PREFS", 0);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("username", userName);
                    editor.putInt("size", 3);
                    editor.apply();

                   startActivity(new Intent(getApplicationContext(), MenuActivity.class));
                   finish();
                }
            }
            public void onCancelled(@NonNull DatabaseError databaseError) {
                login.setText(R.string.log_in);
                login.setEnabled(true);
                findViewById(R.id.warning).setVisibility(View.GONE);
            }
        });
    }
}
