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

    private DatabaseReference servicosReference; // Referência para a tabela "Servicos"
    private DatabaseReference usersReference; // Referência para a tabela "Users"

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
        TextView nomeCliente = findViewById(R.id.nome_cliente); // Campo para exibir o nome do cliente

        // Inicializa as referências ao Firebase Database
        servicosReference = FirebaseDatabase.getInstance().getReference("Servicos");
        usersReference = FirebaseDatabase.getInstance().getReference("Users"); // Referência à tabela de usuários
        Log.d("FirebaseReference", "usersReference inicializada: " + (usersReference != null));

        // Captura o ID do serviço passado pela Intent
        int idServicoValue = getIntent().getIntExtra("ID_SERVICO", -1);
        Log.d("DetalhamentoServico", "ID do Serviço: " + idServicoValue);

        if (idServicoValue != -1) {
            buscarServico(idServicoValue, idServico, descricao, status, observacao, pecas, nomeCliente);
        } else {
            idServico.setText("Serviço não encontrado.");
        }
    }

    private void buscarServico(int idServicoValue, TextView idServico, TextView descricao, TextView status, TextView observacao, TextView pecas, TextView nomeCliente) {
        // Faz a consulta para buscar o serviço pelo ID
        Query query = servicosReference.orderByChild("id").equalTo((double) idServicoValue);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot servicoSnapshot : dataSnapshot.getChildren()) {
                        // Obtém os valores do serviço
                        String descricaoValue = servicoSnapshot.child("descricao").getValue(String.class);
                        String statusValue = servicoSnapshot.child("status").getValue(String.class);
                        String observacaoValue = servicoSnapshot.child("observacoes").getValue(String.class);
                        String pecasValue = servicoSnapshot.child("pecas").getValue(String.class);
                        String cpfValue = servicoSnapshot.child("cpfUser").getValue(String.class); // Obtém o CPF

                        // Exibe os dados do serviço nos TextViews
                        idServico.setText("Serviço: " + idServicoValue);
                        descricao.setText("Descrição: " + descricaoValue);
                        status.setText("Status: " + statusValue);
                       
                        if (observacaoValue == null || observacaoValue.isEmpty()) {
                            observacao.setText("Observações: Não declarado");
                        } else {
                            observacao.setText("Observações: " + observacaoValue);
                        }

                        if (pecasValue == null || pecasValue.isEmpty()) {
                            pecas.setText("Peças: Não declarado");
                        } else {
                            pecas.setText("Peças: " + pecasValue);
                        }

                        // Busca o nome do usuário baseado no CPF
                        if (cpfValue != null) {
                            buscarNomeUsuarioPorCpf(cpfValue, nomeCliente);
                        } else {
                            nomeCliente.setText("Nome: Não declarado");
                        }
                    }
                } else {
                    idServico.setText("Serviço não encontrado.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                idServico.setText("Erro ao buscar serviço: " + databaseError.getMessage());
            }
        });
    }

    private void buscarNomeUsuarioPorCpf(String cpf, TextView nomeCliente) {
        // Verifique o valor do CPF antes de fazer a consulta
        Log.d("DetalhamentoServico", "CPF recebido na função buscarNomeUsuarioPorCpf: " + cpf);

        // Faz a consulta para buscar o usuário pelo CPF
        Query query = usersReference.orderByChild("cpf").equalTo(cpf);
        Log.d("BuscarCPF", "Buscando CPF: " + cpf);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String nomeValue = userSnapshot.child("nome").getValue(String.class);
                        Log.d("DetalhamentoServico", "Nome do usuário encontrado: " + nomeValue);
                        nomeCliente.setText("Nome: " + nomeValue);
                    }
                } else {
                    Log.d("DetalhamentoServico", "Nenhum usuário encontrado com esse CPF");
                    nomeCliente.setText("Nome: Não encontrado");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("DetalhamentoServico", "Erro ao buscar nome: " + databaseError.getMessage());
                nomeCliente.setText("Erro ao buscar nome: " + databaseError.getMessage());
            }
        });
    }
}


