package com.example.flashcards2.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.flashcards2.instances.User;
import com.example.flashcards2.instances.Deck;
import com.example.flashcards2.instances.Flashcard;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database info
    private static final String DATABASE_NAME = "flashcards.db";
    private static final int DATABASE_VERSION = 2;

    // Table names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_DECKS = "decks";
    private static final String TABLE_FLASHCARDS = "flashcards";
    private static final String TABLE_DECKSESSIONS = "deck_sessions";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called once to create the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsers = "CREATE TABLE " + TABLE_USERS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nickname TEXT, " +
                "email TEXT UNIQUE, " +
                "password TEXT)";
        db.execSQL(createUsers);

        String createDecks = "CREATE TABLE " + TABLE_DECKS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "name TEXT, " +
                "FOREIGN KEY(user_id) REFERENCES users(id))";
        db.execSQL(createDecks);

        String createFlashcards = "CREATE TABLE " + TABLE_FLASHCARDS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "deck_id INTEGER, " +
                "question TEXT, " +
                "answer TEXT, " +
                "FOREIGN KEY(deck_id) REFERENCES decks(id))";
        db.execSQL(createFlashcards);

        String createDeckSessions = "CREATE TABLE deck_sessions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "deck_id INTEGER, " +
                "correct_count INTEGER, " +
                "wrong_count INTEGER, " +
                "session_date TEXT DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY(user_id) REFERENCES users(id), " +
                "FOREIGN KEY(deck_id) REFERENCES decks(id))";
        db.execSQL(createDeckSessions);
    }

    // Called if you change DATABASE_VERSION
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FLASHCARDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DECKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DECKSESSIONS);
        onCreate(db);
    }

    // -----------------------------
    // User operations
    // -----------------------------
    public long addUser(String nickname, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nickname", nickname);
        values.put("email", email);
        values.put("password", password);
        return db.insert(TABLE_USERS, null, values);
    }

    public User getUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null,
                "email=? AND password=?",
                new String[]{email, password}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            User user = new User(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("nickname")),
                    cursor.getString(cursor.getColumnIndexOrThrow("email")),
                    cursor.getString(cursor.getColumnIndexOrThrow("password"))
            );
            cursor.close();
            Log.d("DB_DEBUG USER","USERID DBHELPER: " + user.id);
            return user;
        }
        return null;
    }

    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null,
                "id=?",
                new String[]{String.valueOf(userId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            User user = new User(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("nickname")),
                    cursor.getString(cursor.getColumnIndexOrThrow("email")),
                    cursor.getString(cursor.getColumnIndexOrThrow("password"))
            );
            cursor.close();
            return user;
        }

        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    // -----------------------------
    // Deck operations
    // -----------------------------
    public long addDeck(int userId, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("name", name);
        return db.insert(TABLE_DECKS, null, values);
    }

    public List<Deck> getDecksByUser(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Deck> decks = new ArrayList<>();
        Cursor cursor = db.query(TABLE_DECKS, null,
                "user_id=?", new String[]{String.valueOf(userId)},
                null, null, null);

        if (cursor.moveToFirst()) {
            do {
                decks.add(new Deck(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("user_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name"))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return decks;
    }

    public String getDeckNameById(int deckId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String deckName = null;

        Cursor cursor = db.rawQuery("SELECT name FROM decks WHERE id = ?", new String[]{String.valueOf(deckId)});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                deckName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            }
            cursor.close();
        }

        return deckName;
    }

    // -----------------------------
    // Flashcard operations
    // -----------------------------
    public long addFlashcard(int deckId, String question, String answer) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("deck_id", deckId);
        values.put("question", question);
        values.put("answer", answer);
        return db.insert(TABLE_FLASHCARDS, null, values);
    }

    public List<Flashcard> getFlashcardsByDeck(int deckId) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Flashcard> cards = new ArrayList<>();
        Cursor cursor = db.query(TABLE_FLASHCARDS, null,
                "deck_id=?", new String[]{String.valueOf(deckId)},
                null, null, null);

        if (cursor.moveToFirst()) {
            do {
                cards.add(new Flashcard(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("deck_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("question")),
                        cursor.getString(cursor.getColumnIndexOrThrow("answer"))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return cards;
    }

    // -----------------------------
    // Deck Session operations
    // -----------------------------
    public long addDeckSession(int userId, int deckId, int correctCount, int wrongCount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("deck_id", deckId);
        values.put("correct_count", correctCount);
        values.put("wrong_count", wrongCount);
        return db.insert("deck_sessions", null, values);
    }

    // Get stats for a specific user and deck
    public Cursor getDeckStats(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(correct_count) AS total_correct, SUM(wrong_count) AS total_wrong " +
                        "FROM deck_sessions WHERE user_id = ?",
                new String[]{String.valueOf(userId)}
        );

        if (cursor.moveToFirst()) {
            int totalCorrect = cursor.getInt(cursor.getColumnIndexOrThrow("total_correct"));
            int totalWrong = cursor.getInt(cursor.getColumnIndexOrThrow("total_wrong"));
            Log.d("DB_DEBUG", "User " + userId + " → Correct: " + totalCorrect + " | Wrong: " + totalWrong);
            cursor.moveToFirst(); // move back to the start if you plan to read it again outside
        }

        return cursor;
    }

    public Cursor getWeeklySessions(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT DATE(session_date) AS day, COUNT(*) AS sessions " +
                        "FROM deck_sessions " +
                        "WHERE user_id = ? AND session_date >= date('now', '-6 days') " +
                        "GROUP BY DATE(session_date) " +
                        "ORDER BY DATE(session_date)",
                new String[]{String.valueOf(userId)}
        );
    }

    //debug
    public void printAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String nickname = cursor.getString(cursor.getColumnIndexOrThrow("nickname"));
                String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
                String password = cursor.getString(cursor.getColumnIndexOrThrow("password"));

                Log.d("DatabaseHelper", "User ID: " + id +
                        ", Nickname: " + nickname +
                        ", Email: " + email +
                        ", Password: " + password);
            } while (cursor.moveToNext());
        } else {
            Log.d("DatabaseHelper", "No users found in the table.");
        }

        cursor.close();
    }
}
