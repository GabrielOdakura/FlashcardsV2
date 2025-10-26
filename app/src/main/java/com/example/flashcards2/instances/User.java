package com.example.flashcards2.instances;

public class User {
    public int id;
    public String nickname;
    public String email;
    public String password;

    public User(int id, String nickname, String email, String password) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
    }
}
