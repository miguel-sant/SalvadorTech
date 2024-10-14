package com.example.salvadortech;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
        inputCpf = findViewById(R.id.cpf_input); // EditText para CPF

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
        String cpfUser = inputCpf.getText().toString().trim();

        // Log para verificar os valores
        Log.d("AddServicoActivity", "Descrição: " + descricao);
        Log.d("AddServicoActivity", "Status: " + status);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Log.e("AddServicoActivity", "Usuário não autenticado");
            return;
        }

        if (TextUtils.isEmpty(descricao) || TextUtils.isEmpty(status)) {
            Log.e("AddServicoActivity", "Descrição ou status vazios");
            return;
        }

        Servico servico = new Servico(descricao, status, observacoes, pecas, cpfUser);

        databaseReference.push().setValue(servico)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("AddServicoActivity", "Serviço adicionado com sucesso");
                        // Redirecionar para HomeActivity
                        Intent intent = new Intent(AddServicoActivity.this, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Limpa a pilha de atividades
                        startActivity(intent);
                        finish(); // Finaliza a atividade atual
                    } else {
                        Log.e("AddServicoActivity", "Falha ao adicionar serviço: " + task.getException().getMessage());
                    }
                });
    }
}
