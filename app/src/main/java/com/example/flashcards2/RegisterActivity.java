package com.example.flashcards2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flashcards2.utils.DatabaseHelper;

public class RegisterActivity extends AppCompatActivity {

    private EditText nicknameInput, emailInput, passwordInput;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nicknameInput = findViewById(R.id.nicknameInputRegister);
        emailInput = findViewById(R.id.emailInputRegister);
        passwordInput = findViewById(R.id.passwordInputRegister);
        registerButton = findViewById(R.id.registerButtonRegister);

        registerButton.setOnClickListener(v -> handleRegister());
    }

    private void handleRegister() {
        String nickname = nicknameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (nickname.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseHelper db = new DatabaseHelper(this);
        long result = db.addUser(nickname, email, password);

        if (result != -1) {
            Toast.makeText(this, "User registered!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Email already exists", Toast.LENGTH_SHORT).show();
        }
    }
}