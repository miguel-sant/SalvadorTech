package com.example.salvadortech;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.salvadortech.DetalhamentoServicoActivity;

public class EditarServicoActivity extends AppCompatActivity {

    private EditText editTextDescricao, editTextObservacao, editTextPecas;
    private Spinner spinnerStatus;
    private Button buttonSalvar;
    private DatabaseReference servicosReference;
    private int idServico; // Mude para int
    private ImageView homeBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_servico);;

        // Referenciando o ImageView
        homeBottom = findViewById(R.id.home_bottom);

        // Definindo o OnClickListener
        homeBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditarServicoActivity.this, HomeActivity.class);
                startActivity(intent);
                finish(); // Opcional: Fecha a atividade atual se você não quiser que o usuário retorne a ela
            }
        });

        editTextDescricao = findViewById(R.id.editTextDescricao);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        editTextObservacao = findViewById(R.id.editTextObservacao);
        editTextPecas = findViewById(R.id.editTextPecas);
        buttonSalvar = findViewById(R.id.buttonSalvar);

        // Inicializa a referência ao Firebase Database
        servicosReference = FirebaseDatabase.getInstance().getReference("Servicos");

        // Captura o ID do serviço passado pela Intent
        idServico = getIntent().getIntExtra("ID_SERVICO", -1); // Agora é um int
        Log.d("EditarServico", "ID do serviço recebido: " + idServico);

        if (idServico == -1) {
            Toast.makeText(this, "ID do serviço não encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Configura o Spinner com as opções de status
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.status_options, R.layout.spinner_item); // Use o layout personalizado
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter);
        spinnerStatus.setSelection(0);

        // Chama o método para carregar os dados do serviço
        carregarDadosServico(idServico); // Passe o ID para a função

        // Configura o listener para o botão Salvar
        buttonSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                atualizarServico(idServico);
            }
        });
    }

    private void carregarDadosServico(int idServicoValue) {
        Log.d("EditarServico", "Consultando serviço com ID: " + idServicoValue);

        // Faz a consulta para buscar o serviço pelo campo 'id'
        servicosReference.orderByChild("id").equalTo(idServicoValue).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String descricaoValue = snapshot.child("descricao").getValue(String.class);
                        String statusValue = snapshot.child("status").getValue(String.class);
                        String observacaoValue = snapshot.child("observacoes").getValue(String.class);
                        String pecasValue = snapshot.child("pecas").getValue(String.class);

                        // Preenche os campos
                        editTextDescricao.setText(descricaoValue);
                        editTextObservacao.setText(observacaoValue);
                        editTextPecas.setText(pecasValue);

                        // Define a seleção do spinner com o status
                        if (statusValue != null) {
                            spinnerStatus.setSelection(getIndex(spinnerStatus, statusValue));
                        }
                    }
                } else {
                    Log.d("EditarServico", "Serviço não encontrado para o ID: " + idServicoValue);
                    Toast.makeText(EditarServicoActivity.this, "Serviço não encontrado.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(EditarServicoActivity.this, "Erro ao carregar dados: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int getIndex(Spinner spinner, String status) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(status)) {
                return i; // Retorna o índice encontrado
            }
        }
        return 0; // Retorna 0 se não encontrar
    }

    private void atualizarServico(int idServico) {
        // Referência ao nó do serviço com o ID específico
        servicosReference.orderByChild("id").equalTo(idServico).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Obtém o nó do serviço correspondente
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Atualiza os dados do serviço
                        String descricao = editTextDescricao.getText().toString();
                        String status = spinnerStatus.getSelectedItem().toString();
                        String observacao = editTextObservacao.getText().toString();
                        String pecas = editTextPecas.getText().toString();

                        Log.d("SalvarServico", "Salvando serviço: " + idServico);
                        Log.d("SalvarServico", "Descrição: " + descricao);
                        Log.d("SalvarServico", "Status: " + status);
                        Log.d("SalvarServico", "Observação: " + observacao);
                        Log.d("SalvarServico", "Peças: " + pecas);

                        snapshot.child("descricao").getRef().setValue(descricao);
                        snapshot.child("status").getRef().setValue(status);
                        snapshot.child("observacoes").getRef().setValue(observacao);
                        snapshot.child("pecas").getRef().setValue(pecas)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(EditarServicoActivity.this, "Serviço atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(EditarServicoActivity.this, HomeActivity.class); // Altere "HomeActivity" para o nome correto da sua tela inicial
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Opcional: limpa a pilha de atividades
                                        startActivity(intent); // Inicia a tela Home
                                        finish(); // Fecha a atividade atual (opcional, pode ser deixado)
                                    } else {
                                        Toast.makeText(EditarServicoActivity.this, "Erro ao atualizar serviço.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                } else {
                    Toast.makeText(EditarServicoActivity.this, "Serviço não encontrado.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("AtualizarServico", "Erro ao buscar serviço: " + databaseError.getMessage());
            }
        });
    }
}
