package com.example.flashcards2;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flashcards2.instances.CustomStatsView;
import com.example.flashcards2.instances.User;
import com.example.flashcards2.instances.WeeklySessionsView;
import com.example.flashcards2.utils.DatabaseHelper;

import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    private ImageView userPhoto;
    private TextView nicknameText, percentageText;
    private CustomStatsView statsCanvas;
    private WeeklySessionsView weeklySessionsView;
    private DatabaseHelper db;
    private int userId;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userPhoto = findViewById(R.id.userPhotoProfile);
        nicknameText = findViewById(R.id.nicknameTextProfile);
        percentageText = findViewById(R.id.percentageTextProfile);
        statsCanvas = findViewById(R.id.statsCanvas);
        weeklySessionsView = findViewById(R.id.weeklySessionsView);

        SharedPreferences prefs = getSharedPreferences("FlashcardsPrefs", MODE_PRIVATE);
        userId = prefs.getInt("userId", -1);

        db = new DatabaseHelper(this);

        user = db.getUserById(userId);

        nicknameText.setText(user.nickname);
        percentageText.setText(calcPercentage());

        loadUserStats();
        weeklySessionsView.init(db, userId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("FlashcardsPrefs", MODE_PRIVATE);
        userId = prefs.getInt("userId", -1);
        db = new DatabaseHelper(this);
        user = db.getUserById(userId);
        // Reload data from database
        loadUserStats();
        weeklySessionsView.init(db, userId);

        // Update UI texts if needed
        nicknameText.setText(user.nickname);

        // Optional: show a quick toast
        Toast.makeText(this, "Profile refreshed", Toast.LENGTH_SHORT).show();
    }

    private void loadUserStats() {
        Cursor c = db.getDeckStats(userId);
        int totalCorrect = 0;
        int totalWrong = 0;

        if (c.moveToFirst()) {
            totalCorrect = c.getInt(c.getColumnIndexOrThrow("total_correct"));
            totalWrong = c.getInt(c.getColumnIndexOrThrow("total_wrong"));
        }
        c.close();

        statsCanvas.setStats(totalCorrect, totalWrong);
    }

    private String calcPercentage(){
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
}