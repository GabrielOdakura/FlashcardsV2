package com.example.flashcards2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flashcards2.instances.User;
import com.example.flashcards2.utils.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button loginButton, registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.emailInputLogin);
        passwordInput = findViewById(R.id.passwordInputLogin);
        loginButton = findViewById(R.id.loginButtonLogin);
        registerButton = findViewById(R.id.registerButtonLogin);

        loginButton.setOnClickListener(v -> handleLogin());
        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void handleLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseHelper db = new DatabaseHelper(this);
        User user = db.getUser(email, password);

        if (user != null) {
            SharedPreferences prefs = getSharedPreferences("FlashcardsPrefs", MODE_PRIVATE);
            prefs.edit().putInt("userId", user.id).apply();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }
}
