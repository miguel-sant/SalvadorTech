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

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private FirebaseUser currentUser;
    private Button botaoEditar;
    private ImageView homeBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhamento);


        // Referenciando o ImageView
        homeBottom = findViewById(R.id.home_bottom);

        // Definindo o OnClickListener
        homeBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetalhamentoServicoActivity.this, HomeActivity.class);
                startActivity(intent);
                finish(); // Opcional: Fecha a atividade atual se você não quiser que o usuário retorne a ela
            }
        });

        // Inicializa as referências ao Firebase Database e ao usuário autenticado
        servicosReference = FirebaseDatabase.getInstance().getReference("Servicos");
        usersReference = FirebaseDatabase.getInstance().getReference("Users");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Acessa os TextViews e o botão
        TextView idServico = findViewById(R.id.id_servico);
        TextView descricao = findViewById(R.id.descricao);
        TextView status = findViewById(R.id.status);
        TextView observacao = findViewById(R.id.observacao);
        TextView pecas = findViewById(R.id.pecas);
        TextView nomeCliente = findViewById(R.id.nome_cliente);
        botaoEditar = findViewById(R.id.botao_editar);

        // Captura o ID do serviço passado pela Intent
        int idServicoValue = getIntent().getIntExtra("ID_SERVICO", -1);

        // Verifica o ID do serviço e busca as informações
        if (idServicoValue != -1) {
            buscarServico(idServicoValue, idServico, descricao, status, observacao, pecas, nomeCliente);
        } else {
            idServico.setText("Serviço não encontrado.");
        }

        // Verifica se o usuário é admin
        verificarUsuarioAdmin();

        botaoEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetalhamentoServicoActivity.this, EditarServicoActivity.class);
                intent.putExtra("ID_SERVICO", idServicoValue); // Passa o ID do serviço como int
                startActivity(intent);
            }
        });

    }

    private void verificarUsuarioAdmin() {
        if (currentUser != null) {
            // Consulta o banco de dados para verificar se o usuário é admin
            String uid = currentUser.getUid();
            usersReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Integer adminValue = dataSnapshot.child("admin").getValue(Integer.class);
                        if (adminValue != null && adminValue == 1) {
                            botaoEditar.setVisibility(View.VISIBLE); // Exibe o botão se for admin
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("DetalhamentoServico", "Erro ao verificar admin: " + databaseError.getMessage());
                }
            });
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

                        // Formata o texto para observações
                        String obsText = "Observações do técnico: ";
                        String obsContent = (observacaoValue == null || observacaoValue.isEmpty()) ? "Não declarado" : observacaoValue;
                        SpannableString spannableObs = new SpannableString(obsText + obsContent);
                        spannableObs.setSpan(new StyleSpan(Typeface.BOLD), 0, obsText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        observacao.setText(spannableObs); // Aplica o texto formatado

                        // Formata o texto para peças
                        String pecasText = "Peças adicionadar/trocadas: ";
                        String pecasContent = (pecasValue == null || pecasValue.isEmpty()) ? "Não declarado" : pecasValue;
                        SpannableString spannablePecas = new SpannableString(pecasText + pecasContent);
                        spannablePecas.setSpan(new StyleSpan(Typeface.BOLD), 0, pecasText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        pecas.setText(spannablePecas); // Aplica o texto formatado

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


