package com.example.salvadortech;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
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

    private int idServicoValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhamento);

        // Acessa os TextViews e o botão
        TextView idServico = findViewById(R.id.id_servico);
        TextView descricao = findViewById(R.id.descricao);
        TextView status = findViewById(R.id.status);
        TextView observacao = findViewById(R.id.observacao);
        TextView pecas = findViewById(R.id.pecas);
        TextView nomeCliente = findViewById(R.id.nome_cliente);
        Button botaoEditar = findViewById(R.id.botao_editar);

        // Inicializa as referências ao Firebase Database
        servicosReference = FirebaseDatabase.getInstance().getReference("Servicos");
        usersReference = FirebaseDatabase.getInstance().getReference("Users");

        // Captura o ID do serviço passado pela Intent
        int idServicoValue = getIntent().getIntExtra("ID_SERVICO", -1);

        if (idServicoValue != -1) {
            buscarServico(idServicoValue, idServico, descricao, status, observacao, pecas, nomeCliente);
        } else {
            idServico.setText("Serviço não encontrado.");
        }

        // Verifica se o usuário é administrador
        verificarUsuarioAdmin(botaoEditar);
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

                        String observacaoText;
                        if (observacaoValue == null || observacaoValue.isEmpty()) {
                            observacaoText = "Observações do técnico: Não declarado";
                        } else {
                            observacaoText = "Observações do técnico: " + observacaoValue;
                        }

                        SpannableString spannableObservacao = new SpannableString(observacaoText);
                        spannableObservacao.setSpan(new StyleSpan(Typeface.BOLD), 0, 24, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // 25 é o comprimento do texto "Observações do técnico: "

                        observacao.setText(spannableObservacao);

                        String pecasText;
                        if (pecasValue == null || pecasValue.isEmpty()) {
                            pecasText = "Peças necessárias ou substituídas: Não declarado";
                        } else {
                            pecasText = "Peças necessárias ou substituídas: " + pecasValue;
                        }

                        SpannableString spannablePecas = new SpannableString(pecasText);
                        spannablePecas.setSpan(new StyleSpan(Typeface.BOLD), 0, 33, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // 33 é o comprimento do texto "Peças necessárias ou substituídas: "

                        pecas.setText(spannablePecas);


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

    private void verificarUsuarioAdmin(Button botaoEditar) {
        // Supondo que você tenha o CPF do usuário logado armazenado
        String cpfUsuarioLogado = "08562515507"; // substitua pelo CPF real do usuário logado

        Query query = usersReference.orderByChild("cpf").equalTo(cpfUsuarioLogado);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("DetalhamentoServico", "ID do Serviço ao editar: " + idServicoValue);
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        Integer adminValue = userSnapshot.child("admin").getValue(Integer.class);
                        if (adminValue != null && adminValue == 1) {
                            // Usuário é admin, mostra o botão
                            botaoEditar.setVisibility(View.VISIBLE);


                            botaoEditar.setOnClickListener(v -> {
                                Log.d("DetalhamentoServico", "ID do Serviço ao editar: " + idServicoValue);
                                Intent intent = new Intent(DetalhamentoServicoActivity.this, EditarServicoActivity.class);
                                intent.putExtra("ID_SERVICO", idServicoValue);
                                startActivity(intent);
                            });
                        } else {
                            // Usuário não é admin, oculta o botão
                            botaoEditar.setVisibility(View.GONE);
                        }
                    }
                } else {
                    // Se o usuário não for encontrado, oculta o botão
                    botaoEditar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Trate o erro, se necessário
                Log.e("DetalhamentoServico", "Erro ao verificar admin: " + databaseError.getMessage());
                botaoEditar.setVisibility(View.GONE);
            }
        });
    }

}


