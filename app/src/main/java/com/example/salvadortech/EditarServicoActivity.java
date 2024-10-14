package com.example.salvadortech;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditarServicoActivity extends AppCompatActivity {

    private EditText editTextDescricao, editTextObservacao, editTextPecas;
    private Spinner spinnerStatus;
    private Button buttonSalvar;
    private DatabaseReference servicosReference;
    private int idServico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_servico);

        editTextDescricao = findViewById(R.id.editTextDescricao);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        editTextObservacao = findViewById(R.id.editTextObservacao);
        editTextPecas = findViewById(R.id.editTextPecas);
        buttonSalvar = findViewById(R.id.buttonSalvar);

        // Inicializa a referência ao Firebase Database
        servicosReference = FirebaseDatabase.getInstance().getReference("Servicos");

        // Captura o ID do serviço passado pela Intent
        String idServico = getIntent().getStringExtra("ID_SERVICO");
        Log.d("EditarServico", "ID do Serviço recebido: " + idServico);

        // Configura o Spinner com as opções de status
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.status_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter);

        // Configura o listener para o botão Salvar
        buttonSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarServico();
            }
        });

        // Chama o método para carregar os dados do serviço
        carregarDadosServico(); // Adicione esta linha
    }

    private void carregarDadosServico() {
        // Faz a consulta para buscar o serviço pelo ID
        Log.d("EditarServico", "Buscando serviço com ID: " + idServico);
        servicosReference.child(String.valueOf(idServico)).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Obtém os valores do serviço
                    String descricaoValue = dataSnapshot.child("descricao").getValue(String.class);
                    String statusValue = dataSnapshot.child("status").getValue(String.class);
                    String observacaoValue = dataSnapshot.child("observacoes").getValue(String.class);
                    String pecasValue = dataSnapshot.child("pecas").getValue(String.class);

                    // Preenche os campos
                    editTextDescricao.setText(descricaoValue);
                    editTextObservacao.setText(observacaoValue);
                    editTextPecas.setText(pecasValue);

                    // Define a seleção do spinner com o status
                    if (statusValue != null) {
                        spinnerStatus.setSelection(getIndex(spinnerStatus, statusValue));
                    }
                } else {
                    Log.d("EditarServico", "Serviço não encontrado para o ID: " + idServico);
                    Toast.makeText(EditarServicoActivity.this, "Serviço não encontrado.", Toast.LENGTH_SHORT).show();
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
                return i;
            }
        }
        return 0; // Retorna 0 se não encontrar, para evitar crash
    }

    private void salvarServico() {
        String descricao = editTextDescricao.getText().toString();
        String status = spinnerStatus.getSelectedItem().toString(); // Obtém o status selecionado
        String observacao = editTextObservacao.getText().toString();
        String pecas = editTextPecas.getText().toString();

        // Atualiza os dados do serviço no Firebase
        servicosReference.child(String.valueOf(idServico)).child("descricao").setValue(descricao);
        servicosReference.child(String.valueOf(idServico)).child("status").setValue(status);
        servicosReference.child(String.valueOf(idServico)).child("observacoes").setValue(observacao);
        servicosReference.child(String.valueOf(idServico)).child("pecas").setValue(pecas)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(EditarServicoActivity.this, "Serviço atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                        finish(); // Fecha a atividade atual
                    } else {
                        Toast.makeText(EditarServicoActivity.this, "Erro ao atualizar serviço.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
