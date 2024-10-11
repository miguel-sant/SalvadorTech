package com.example.salvadortech;

public class Servico {
    private int id; // ID do serviço
    private String descricao;
    private String status;
    private String observacoes;
    private String pecas;


    // Construtor vazio necessário para o Firebase
    public Servico() {}

    public Servico(String descricao, String status, String observacoes, String pecas) {
        this.descricao = descricao;
        this.status = status;
        this.observacoes = observacoes;
        this.pecas = pecas;
    }

    // Getters e Setters
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public String getPecas() {
        return pecas;
    }

    public void setPecas(String pecas) {
        this.pecas = pecas;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}