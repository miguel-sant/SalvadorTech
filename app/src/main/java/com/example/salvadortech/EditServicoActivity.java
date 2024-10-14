package com.example.salvadortech;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class EditServicoActivity extends AppCompatActivity {

    private EditText inputDescricao, inputObservacoes, inputPecas, inputCpf;
    private Spinner statusSpinner;
    private Button buttonUpdate;
    private DatabaseReference databaseReference;

    private String servicoId; // ID do serviço a ser editado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_servico); // Reutilizando o layout de adicionar

        // Inicializando os componentes da interface
        inputDescricao = findViewById(R.id.descricao_input);
        statusSpinner = findViewById(R.id.status_spinner);
        inputObservacoes = findViewById(R.id.observacao_input);
        inputPecas = findViewById(R.id.pecas_input);
        inputCpf = findViewById(R.id.cpf_input);
        buttonUpdate = findViewById(R.id.save_service_button);

        // Inicializando a referência do Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("Servicos");

        // Obtendo o ID do serviço da intent
        servicoId = getIntent().getStringExtra("SERVICO_ID");

        // Carregar os dados do serviço
        carregarDadosServico();

        // Configurando o botão de atualização
        buttonUpdate.setOnClickListener(v -> atualizarServico());
    }

    private void carregarDadosServico() {
        databaseReference.child(servicoId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Servico servico = dataSnapshot.getValue(Servico.class);
                    if (servico != null) {
                        // Preencher os campos com os dados existentes
                        inputDescricao.setText(servico.getDescricao());
                        inputObservacoes.setText(servico.getObservacoes());
                        inputPecas.setText(servico.getPecas());
                        inputCpf.setText(servico.getCpfUser());

                        // Selecionar o status no Spinner
                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                                EditServicoActivity.this,
                                R.array.status_options,
                                R.layout.spinner_item
                        );
                        statusSpinner.setAdapter(adapter);
                        int spinnerPosition = adapter.getPosition(servico.getStatus());
                        statusSpinner.setSelection(spinnerPosition);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Lidar com erros aqui
            }
        });
    }

    private void atualizarServico() {
        String descricao = inputDescricao.getText().toString().trim();
        String status = statusSpinner.getSelectedItem().toString();
        String observacoes = inputObservacoes.getText().toString().trim();
        String pecas = inputPecas.getText().toString().trim();
        String cpfUser = inputCpf.getText().toString().trim();

        // Verificar se os campos obrigatórios estão preenchidos
        if (TextUtils.isEmpty(descricao) || TextUtils.isEmpty(status)) {
            return;
        }

        // Atualizando os dados no Firebase
        Servico servico = new Servico(descricao, status, observacoes, pecas, cpfUser);
        databaseReference.child(servicoId).setValue(servico)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sucesso, talvez fechar a activity ou mostrar uma mensagem
                    } else {
                        // Falha, tratar o erro
                    }
                });
    }
}
