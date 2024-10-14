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
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Chamado chamado);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public ChamadoAdapter(List<Chamado> chamadosList) {
        this.chamadosList = chamadosList;
    }

    @Override
    public ChamadoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_chamado, parent, false);
        return new ChamadoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ChamadoViewHolder holder, int position) {
        Chamado chamado = chamadosList.get(position);
        holder.titleTextView.setText(chamado.getDescricao());
        holder.statusTextView.setText(chamado.getStatus());
    }

    @Override
    public int getItemCount() {
        return chamadosList.size();
    }

    public class ChamadoViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView, statusTextView;

        public ChamadoViewHolder(View view) {
            super(view);
            titleTextView = view.findViewById(R.id.card_title);
            statusTextView = view.findViewById(R.id.card_status);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(chamadosList.get(position));
                    }
                }
            });
        }
    }
}