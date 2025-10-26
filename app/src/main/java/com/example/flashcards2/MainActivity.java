package com.example.flashcards2;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flashcards2.instances.Deck;
import com.example.flashcards2.instances.User;
import com.example.flashcards2.utils.DatabaseHelper;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ImageView userPhoto;
    private TextView nicknameText, percentageText;
    private GridLayout deckGrid;
    private DatabaseHelper db;
    private User user;
    private int userId;
    private String percentage = "0.0%";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userPhoto = findViewById(R.id.userPhotoMain);
        nicknameText = findViewById(R.id.nicknameTextMain);
        percentageText = findViewById(R.id.percentageTextMain);
        deckGrid = findViewById(R.id.gridContainerMain);

        int userId;
        SharedPreferences prefs = getSharedPreferences("FlashcardsPrefs", MODE_PRIVATE);
        userId = prefs.getInt("userId", -1);
        Log.d("MainActivity", "Main userId=" + userId);

        userPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });

        db = new DatabaseHelper(this);
        List<Deck> decks = db.getDecksByUser(userId);

        user = db.getUserById(userId);
        nicknameText.setText(user.nickname);

        loadDecks();

        percentage = calcPercentage();
        percentageText.setText(percentage);

        // adiciona um deck novo quando você segura na tela
        deckGrid.setOnLongClickListener(v -> {
            showCreateDeckDialog();
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Re-read current user ID from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("FlashcardsPrefs", MODE_PRIVATE);
        userId = prefs.getInt("userId", -1);
        Log.d("MainActivity", "onResume userId=" + userId);

        // Reload user from DB
        db = new DatabaseHelper(this);
        user = db.getUserById(userId);

        if (user != null) {
            // Update UI
            nicknameText.setText(user.nickname);
        } else {
            nicknameText.setText("Unknown User");
            Log.w("MainActivity", "User not found for ID=" + userId);
        }

        // Reload decks & stats
        loadDecks();

        percentage = calcPercentage();
        percentageText.setText(percentage);
    }

    private void showCreateDeckDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Novo Deck");

        final EditText input = new EditText(this);
        input.setHint("Nome do Deck");
        builder.setView(input);

        builder.setPositiveButton("Criar", (dialog, which) -> {
            String deckName = input.getText().toString().trim();
            if (!deckName.isEmpty()) {
                long deckId = db.addDeck(userId, deckName);
                Deck newDeck = new Deck((int) deckId, userId, deckName);
                addDeckCard(newDeck);

                showSimpleFlashcardDialog((int) deckId);
            }
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void showSimpleFlashcardDialog(int deckId) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Novo Flashcard");

        // Caixa para pergunta
        final EditText questionInput = new EditText(this);
        questionInput.setHint("Pergunta");
        builder.setView(questionInput);

        builder.setPositiveButton("Próximo", (dialog, which) -> {
            String question = questionInput.getText().toString().trim();
            if (question.isEmpty()) return;

            // Agora pede a resposta (um segundo diálogo)
            MaterialAlertDialogBuilder answerBuilder = new MaterialAlertDialogBuilder(this);
            answerBuilder.setTitle("Resposta para:");
            answerBuilder.setMessage(question);

            final EditText answerInput = new EditText(this);
            answerInput.setHint("Resposta");
            answerBuilder.setView(answerInput);

            answerBuilder.setPositiveButton("Salvar", (d, w) -> {
                String answer = answerInput.getText().toString().trim();
                if (!answer.isEmpty()) {
                    db.addFlashcard(deckId, question, answer);
                    Toast.makeText(this, "Flashcard criado!", Toast.LENGTH_SHORT).show();
                }
            });

            answerBuilder.setNegativeButton("Cancelar", null);
            answerBuilder.show();
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void loadDecks() {
        List<Deck> decks = db.getDecksByUser(userId);
        deckGrid.removeAllViews(); //limpa para evitar conflitos

        for (Deck deck : decks) {
            addDeckCard(deck);
        }
    }

    private void addDeckCard(Deck deck) {
        TextView card = new TextView(this);
        card.setText(deck.name);
        card.setTextSize(18f);
        card.setGravity(Gravity.CENTER);
        card.setPadding(8, 0, 32, 32);
        card.setBackgroundResource(R.drawable.box_background);
        card.setClickable(true);

        int size = dpToPx(160);
        card.setWidth(size);
        card.setHeight(size);

        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));
        card.setLayoutParams(params);

        // On click -> open DeckActivity
        card.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DeckActivity.class);
            intent.putExtra("deckId", deck.id);
//            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        deckGrid.addView(card);
    }

    private String calcPercentage(){
        Log.d("MAINACTV DEBUG PERCENTAGE","USERID " + userId);
        Cursor c = db.getDeckStats(userId);
        String percentage = "0.0%"; // default in case no stats yet

        if (c.moveToFirst()) {
            int totalCorrect = c.getInt(c.getColumnIndexOrThrow("total_correct"));
            int totalWrong = c.getInt(c.getColumnIndexOrThrow("total_wrong"));
            int total = totalCorrect + totalWrong;

            if (total > 0) {
                double percent = (totalCorrect * 100.0) / total;
                percentage = String.format(Locale.US, "%.1f%%", percent);
            }
        }

        c.close();
        return percentage;
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}