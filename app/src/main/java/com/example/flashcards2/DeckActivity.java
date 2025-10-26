package com.example.flashcards2;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flashcards2.instances.Flashcard;
import com.example.flashcards2.utils.DatabaseHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeckActivity extends AppCompatActivity {

    private Button addFlashcardButton, confirmButton;
    private TextView deckTitle, flashcardCount, questionText, showAnswerText;
    private EditText answerInput;
    private DatabaseHelper db;
    private List<Flashcard> cards;
    private List<Flashcard> shuffledCards;
    private Flashcard selectedCard;

    private boolean showingAnswer = false;
    private int cardCount = 0;
    private int correctAnwsers = 0, wrongAwnsers = 0;
    private int deckId, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deck);

        addFlashcardButton = findViewById(R.id.addFlashcardButton);
        confirmButton = findViewById(R.id.confirmButton);
        deckTitle = findViewById(R.id.deckTitle);
        flashcardCount = findViewById(R.id.flashcardCount);
        questionText = findViewById(R.id.questionText);
        showAnswerText = findViewById(R.id.showAnswerText);
        answerInput = findViewById(R.id.answerInput);

        SharedPreferences prefs = getSharedPreferences("FlashcardsPrefs", MODE_PRIVATE);
        userId = prefs.getInt("userId", -1);
        deckId = getIntent().getIntExtra("deckId", -1);
//        userId = getIntent().getIntExtra("userId", -1);
        db = new DatabaseHelper(this);
        cards = db.getFlashcardsByDeck(deckId);

        deckTitle.setText(db.getDeckNameById(deckId));

        addFlashcardButton.setOnClickListener(v -> showAddFlashcardDialog(deckId));
        confirmButton.setOnClickListener(v -> handleConfirm());
        showAnswerText.setOnClickListener(v -> toggleShowAnswer());
        nextCard();
    }

    private void showAddFlashcardDialog(int deckId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Novo Flashcard");

        final EditText inputQuestion = new EditText(this);
        inputQuestion.setHint("Insira a Pergunta");

        builder.setView(inputQuestion);
        builder.setPositiveButton("Proximo", (dialog, which) -> {
            final EditText inputAnswer = new EditText(this);
            inputAnswer.setHint("Digite a Resposta");

            new AlertDialog.Builder(this)
                    .setTitle("Resposta")
                    .setView(inputAnswer)
                    .setPositiveButton("Salvar", (d2, w2) -> {
                        String question = inputQuestion.getText().toString();
                        String answer = inputAnswer.getText().toString();
                        db.addFlashcard(deckId, question, answer);
                        refreshCards();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void handleConfirm() {
        if (!showingAnswer) {
            showingAnswer = true;
            confirmButton.setText("Next");
            String anwser = String.valueOf(answerInput.getText());
            if (anwser.equalsIgnoreCase(selectedCard.answer)){
                correctAnwsers += 1;
                showAnwser(true);
            }else {
                wrongAwnsers += 1;
                showAnwser(false);
            }

        } else {
            showingAnswer = false;
            confirmButton.setText("Confirm");
            nextCard();
        }
    }

    private void toggleShowAnswer() {
        if (!showingAnswer) {
            wrongAwnsers += 1;
            showingAnswer = true;
            confirmButton.setText("Next");
            showAnwser(false);
        } else {
            showingAnswer = false;
            confirmButton.setText("Confirm");
            nextCard();
        }
    }

    private void showAnwser(boolean correctAnwser){
        questionText.setText(selectedCard.answer);
        answerInput.setVisibility(View.INVISIBLE);
        answerInput.setText("");
        if (correctAnwser){
            GradientDrawable background = (GradientDrawable) questionText.getBackground().mutate();
            background.setColor(Color.parseColor("#559b4d"));
            questionText.setTextColor(Color.parseColor("#ffffff"));
            showAnswerText.setTextColor(Color.parseColor("#ffffff"));
            showAnswerText.setText("Acertou!");
        }else{
            GradientDrawable background = (GradientDrawable) questionText.getBackground().mutate();
            background.setColor(Color.parseColor("#f13e38"));
            questionText.setTextColor(Color.parseColor("#ffffff"));
            showAnswerText.setTextColor(Color.parseColor("#ffffff"));
            showAnswerText.setText("Tente lembrar da resposta!");
        }
        if(cardCount + 1 > shuffledCards.size()) {
            confirmButton.setText("Voltar");
            sendStatistics();
            confirmButton.setOnClickListener(v -> {
                finish();
            });
        }
    }

    private void nextCard(){
        if (selectedCard == null) {
            shuffledCards = new ArrayList<>(cards);
            Collections.shuffle(shuffledCards);
        }
        selectedCard = shuffledCards.get(cardCount);
        cardCount += 1;
        if(cardCount > shuffledCards.size()){
            cardCount -= 1;
            confirmButton.setText("Voltar");
            sendStatistics();
            confirmButton.setOnClickListener(v -> {
                finish();
            });
        }else {
            String stringCount = (cardCount) + "/" + shuffledCards.size();
            flashcardCount.setText(stringCount);
            questionText.setText(selectedCard.question);
            GradientDrawable background = (GradientDrawable) questionText.getBackground().mutate();
            background.setColor(Color.parseColor("#555555"));
            questionText.setTextColor(Color.parseColor("#ffffff"));
            showAnswerText.setTextColor(Color.parseColor("#ffffff"));
            showAnswerText.setText("Toque para ver a resposta");
            answerInput.setVisibility(View.VISIBLE);
        }
    }

    private void sendStatistics(){
        db.addDeckSession(userId, deckId, correctAnwsers, wrongAwnsers);
    }

    private void refreshCards() {
        correctAnwsers = 0;
        wrongAwnsers = 0;
        cardCount = 0;

        selectedCard = null;

        cards = db.getFlashcardsByDeck(deckId);

        shuffledCards = new ArrayList<>(cards);
        Collections.shuffle(shuffledCards);

        nextCard();
    }
}
