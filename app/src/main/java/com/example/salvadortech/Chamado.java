package com.example.salvadortech;

public class Chamado {
    private Long id; // Use Long em vez de int
    private String descricao;
    private String status;
    private String observacoes;
    private String pecas;

    // Construtores, getters e setters
    public Chamado() {
        // Construtor vazio necessário para o Firebase
    }

    public Chamado(Long id, String descricao, String status, String observacoes, String pecas) {
        this.id = id;
        this.descricao = descricao;
        this.status = status;
        this.observacoes = observacoes;
        this.pecas = pecas;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
}
