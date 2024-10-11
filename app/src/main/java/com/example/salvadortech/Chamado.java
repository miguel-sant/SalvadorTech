public class Chamado {
    private String id;
    private String descricao;
    private String observacoes;
    private String pecas;
    private String status;

    public Chamado(String id, String descricao, String observacoes, String pecas, String status) {
        this.id = id;
        this.descricao = descricao;
        this.observacoes = observacoes;
        this.pecas = pecas;
        this.status = status;
    }

    // Métodos getters (e possivelmente setters) aqui
    public String getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public String getPecas() {
        return pecas;
    }

    public String getStatus() {
        return status;
    }
}
