package com.example.salvadortech;

public class User {
    private String email;

    public User() {
        // Construtor vazio necessário para o Firestore
    }

    public User(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
