package com.example.salvadortech;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddServicoActivity extends AppCompatActivity {

    private EditText inputDescricao, inputObservacoes, inputPecas, inputCpf;
    private Spinner statusSpinner;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_servico);

        inputDescricao = findViewById(R.id.descricao_input); // EditText para descrição do serviço
        statusSpinner = findViewById(R.id.status_spinner); // Spinner para status
        inputObservacoes = findViewById(R.id.observacao_input); // EditText para observações
        inputPecas = findViewById(R.id.pecas_input); // EditText para peças
        inputCpf = findViewById(R.id.cpf_input);

        // Crie um ArrayAdapter com o layout personalizado
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.status_options, // Substitua pelo seu array
                R.layout.spinner_item // Layout personalizado
        );

        // Defina o layout para a lista suspensa
        adapter.setDropDownViewResource(R.layout.spinner_item);

        // Aplique o adapter ao Spinner
        statusSpinner.setAdapter(adapter);

        Button buttonSave = findViewById(R.id.save_service_button); // Botão de salvar

        // Inicializar Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference("Servicos"); // "Servicos" é o nome da coleção

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adicionarServico();
            }
        });
    }

    private void adicionarServico() {
        String descricao = inputDescricao.getText().toString().trim();
        String status = statusSpinner.getSelectedItem().toString();
        String observacoes = inputObservacoes.getText().toString().trim();
        String pecas = inputPecas.getText().toString().trim();
        String cpfUser = inputCpf.getText().toString().trim(); // Novo campo para CPF

        if (TextUtils.isEmpty(descricao) || TextUtils.isEmpty(status) || TextUtils.isEmpty(cpfUser)) {
            Toast.makeText(this, "Por favor, preencha a descrição, o status e o CPF do cliente.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cria um objeto Servico
        Servico servico = new Servico(descricao, status, observacoes, pecas, cpfUser);

        // Usa o push() para gerar um ID único
        databaseReference.push().setValue(servico)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(AddServicoActivity.this, "Serviço adicionado com sucesso!", Toast.LENGTH_SHORT).show();
                        finish(); // Finaliza a activity
                    } else {
                        Toast.makeText(AddServicoActivity.this, "Falha ao adicionar serviço.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
