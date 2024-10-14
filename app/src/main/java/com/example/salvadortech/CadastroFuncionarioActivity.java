package com.example.salvadortech;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CadastroFuncionarioActivity extends AppCompatActivity {

    private EditText editTextNome, editTextEmail, editTextCpf, editTextPassword, editTextRepeatPassword;
    private Button buttonCadastrar;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_funcionario);

        editTextNome = findViewById(R.id.editTextNome);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextCpf = findViewById(R.id.editTextCpf);
        editTextPassword = findViewById(R.id.editPasswordPassword);
        editTextRepeatPassword = findViewById(R.id.editConfirmPassword); // Adicione este campo no XML
        buttonCadastrar = findViewById(R.id.buttonCadastrar);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Inicializar Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        buttonCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cadastrarFuncionario();
            }
        });
    }

    private void cadastrarFuncionario() {
        String nome = editTextNome.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String cpf = editTextCpf.getText().toString().trim();
        String senha = editTextPassword.getText().toString().trim();
        String repetirSenha = editTextRepeatPassword.getText().toString().trim();

        // Tenta criar o usuário no Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Cadastro bem-sucedido, salva os dados no Realtime Database
                        FirebaseUser user = mAuth.getCurrentUser();
                        salvarDadosFuncionario(user, nome, cpf, email); // Chama o método para salvar os dados
                    }
                });
    }

    private void salvarDadosFuncionario(FirebaseUser user, String nome, String cpf, String email) {
        if (user == null) return;

        // Cria um objeto User com o valor de is_admin igual a 1 (é admin)
        User funcionario = new User(nome, cpf, email, 1); // Atribui 1 para is_admin

        // Salva os dados do usuário no nó "Users/{userId}"
        databaseReference.child(user.getUid()).setValue(funcionario)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        finish(); // Finaliza a activity
                    }
                });
    }
}
