package com.example.salvadortech;

public class User {
    public String nome;
    public String cpf;
    public String email;
    public int admin; // Adiciona o campo admin

    // Construtor com campo admin
    public User(String nome, String cpf, String email, int admin) {
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.admin = admin;
    }
}

