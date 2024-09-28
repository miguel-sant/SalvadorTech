package com.example.salvadortech;
import android.database.Cursor;

import android.os.Bundle;
import android.widget.EditText;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

public class CadastroFuncionarioActivity extends AppCompatActivity {

    private EditText editTextNome, editTextEmail, editTextCpf, editPasswordPassword;
    private Button buttonCadastrar;
    private DataBaseFuncionario dbFuncionario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_funcionario);

        editTextNome = findViewById(R.id.editTextNome);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextCpf = findViewById(R.id.editTextCpf);
        editPasswordPassword = findViewById(R.id.editPasswordPassword);
        buttonCadastrar = findViewById(R.id.buttonCadastrar);

        dbFuncionario = new DataBaseFuncionario(this);

        buttonCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cadastrarFuncionario();
            }
        });
    }

    private void cadastrarFuncionario() {
        String nome = editTextNome.getText().toString();
        String email = editTextEmail.getText().toString();
        String cpf = editTextCpf.getText().toString();
        String senha = editPasswordPassword.getText().toString();

        if (nome.isEmpty() || email.isEmpty() || cpf.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isInserted = dbFuncionario.addFuncionario(nome, email, cpf, senha);

        if (isInserted) {
            Toast.makeText(this, "Funcionário cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Erro ao cadastrar funcionário.", Toast.LENGTH_SHORT).show();
        }

        visualizarFuncionarios(); // Chamada para visualizar os dados cadastrados
    }

    private void visualizarFuncionarios() {
        Cursor cursor = dbFuncionario.getAllFuncionarios();
        StringBuilder builder = new StringBuilder();

        while (cursor.moveToNext()) {
            String nome = cursor.getString(cursor.getColumnIndex("nome_completo"));
            String email = cursor.getString(cursor.getColumnIndex("email"));
            String cpf = cursor.getString(cursor.getColumnIndex("cpf"));
            builder.append("Nome: ").append(nome).append(", Email: ").append(email).append(", CPF: ").append(cpf).append("\n");
        }

        cursor.close();

        // Exiba no Log para verificar os dados
        Log.d("FUNCIONARIOS", builder.toString());
    }
}
