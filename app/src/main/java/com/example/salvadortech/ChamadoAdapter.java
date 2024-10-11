package com.example.salvadortech;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ChamadoAdapter extends RecyclerView.Adapter<ChamadoAdapter.ChamadoViewHolder> {

    private List<Chamado> chamadosList;

    public ChamadoAdapter(List<Chamado> chamados) {
        this.chamadosList = chamados;
    }

    @NonNull
    @Override
    public ChamadoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflando o layout do item de chamado
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_chamado, parent, false);
        return new ChamadoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChamadoViewHolder holder, int position) {
        // Obtendo o item de chamado atual da lista
        Chamado chamado = chamadosList.get(position);

        // Vinculando os dados do chamado ao layout
        holder.tituloChamado.setText(chamado.getDescricao());
        holder.descricaoChamado.setText(chamado.getDescricao());
    }

    @Override
    public int getItemCount() {
        return chamadosList.size();
    }

    // ViewHolder para gerenciar o layout do item
    public static class ChamadoViewHolder extends RecyclerView.ViewHolder {

        TextView tituloChamado, descricaoChamado;

        public ChamadoViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ligando os TextViews do layout ao código
            tituloChamado = itemView.findViewById(R.id.tv_titulo_chamado);
            descricaoChamado = itemView.findViewById(R.id.tv_descricao_chamado);
        }
    }
}

