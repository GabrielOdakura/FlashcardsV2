package com.example.flashcards2.instances;

public class Flashcard {
    public int id;
    public int deckId;
    public String question;
    public String answer;

    public Flashcard(int id, int deckId, String question, String answer) {
        this.id = id;
        this.deckId = deckId;
        this.question = question;
        this.answer = answer;
    }
}
