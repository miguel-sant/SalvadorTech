package com.example.salvadortech;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import com.google.firebase.database.*;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class DetalhamentoServicoActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhamento); // Certifique-se de definir o layout correto

        // Acessa os TextViews definidos no XML
        TextView idServico = findViewById(R.id.id_servico);
        TextView descricao = findViewById(R.id.descricao);
        TextView status = findViewById(R.id.status);
        TextView observacao = findViewById(R.id.observacao);
        TextView pecas = findViewById(R.id.pecas);

        // Inicializa a referência ao Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("Servicos");


        // Captura o ID do serviço passado pela Intent
        int idServicoValue = getIntent().getIntExtra("ID_SERVICO", -1); // Use String para o ID
        Log.d("DetalhamentoServico", "ID do Serviço: " + idServicoValue);
//         Busca os dados do serviço no Firebase
        if (idServicoValue != -1) { // Verifica se o ID é válido
            buscarServico(idServicoValue, idServico, descricao, status, observacao, pecas);
        } else {
            idServico.setText("Serviço não encontrado.");
        }
    }

    private void buscarServico(int idServicoValue, TextView idServico, TextView descricao, TextView status, TextView observacao, TextView pecas) {
        // Faz a consulta para buscar o serviço que tenha o campo "id" igual ao idServicoValue
        Query query = databaseReference.orderByChild("id").equalTo((double) idServicoValue);// Converte para double

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Verifica se o serviço foi encontrado
                if (dataSnapshot.exists()) {
                    for (DataSnapshot servicoSnapshot : dataSnapshot.getChildren()) {
                        // Converte os dados para o modelo de serviço
                        String descricaoValue = servicoSnapshot.child("descricao").getValue(String.class);
                        String statusValue = servicoSnapshot.child("status").getValue(String.class);
                        String observacaoValue = servicoSnapshot.child("observacoes").getValue(String.class);
                        String pecasValue = servicoSnapshot.child("pecas").getValue(String.class);

                        // Exibe os dados nos TextViews
                        idServico.setText("Serviço: " + idServicoValue);
                        descricao.setText("Descrição: " + descricaoValue);
                        status.setText("Status: " + statusValue);

                        // Verifica se observacaoValue está vazio ou nulo
                        if (observacaoValue == null || observacaoValue.isEmpty()) {
                            observacao.setText("Observações: Não declarado");
                        } else {
                            observacao.setText("Observações: " + observacaoValue);
                        }

                        // Verifica se pecasValue está vazio ou nulo
                        if (pecasValue == null || pecasValue.isEmpty()) {
                            pecas.setText("Peças: Não declarado");
                        } else {
                            pecas.setText("Peças: " + pecasValue);
                        }
                    }
                } else {
                    idServico.setText("Serviço não encontrado.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Em caso de erro
                idServico.setText("Erro ao buscar serviço: " + databaseError.getMessage());
            }
        });
    }
}

