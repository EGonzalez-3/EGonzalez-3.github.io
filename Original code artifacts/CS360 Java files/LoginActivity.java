package com.example.cs360project2option1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Points to the login screen layout
        setContentView(R.layout.activity_main);

        // 1. Set up the Login Button
        Button loginButton = findViewById(R.id.btn_login);
        loginButton.setOnClickListener(v -> onClick(null));

        // 2. Set up the Register Button
        Button registerButton = findViewById(R.id.btn_register);
        registerButton.setOnClickListener(this::onClick);
    }

    private void onClick(View v) {
        Intent intent = new Intent(LoginActivity.this, DatabaseActivity.class);
        startActivity(intent);
    }
}


