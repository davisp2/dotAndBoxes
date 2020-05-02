package com.example.dotandboxes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
    public void doLogin(View view) {
        EditText nameInput = findViewById(R.id.enterName);
        String userName = nameInput.getText().toString();
        if (!userName.equals("")) {
            Intent intent = new Intent(this, MenuActivity.class);
            intent.putExtra("Username", userName);
            startActivity(intent);
            finish();
        }
    }
}
