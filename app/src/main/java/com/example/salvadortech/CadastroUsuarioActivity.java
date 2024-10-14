package com.example.salvadortech;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CadastroUsuarioActivity extends AppCompatActivity {

    private EditText inputNome, inputEmail, inputCpf, inputPassword, inputRepeatPassword;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_cliente);

        inputNome = findViewById(R.id.input_name); // EditText para o nome
        inputEmail = findViewById(R.id.input_email); // EditText para o email
        inputCpf = findViewById(R.id.input_cpf); // EditText para o CPF
        inputPassword = findViewById(R.id.input_password); // EditText para a senha
        inputRepeatPassword = findViewById(R.id.input_repeat_password); // EditText para repetir a senha
        Button buttonRegister = findViewById(R.id.button_register); // Botão de cadastro

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Inicializar Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificarCadastro();
            }
        });
    }

    private void verificarCadastro() {
        String nome = inputNome.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String cpf = inputCpf.getText().toString().trim();
        String senha = inputPassword.getText().toString().trim();
        String repetirSenha = inputRepeatPassword.getText().toString().trim();

        if (TextUtils.isEmpty(nome) || TextUtils.isEmpty(email) || TextUtils.isEmpty(cpf) || TextUtils.isEmpty(senha) || TextUtils.isEmpty(repetirSenha)) {
            Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!senha.equals(repetirSenha)) {
            Toast.makeText(this, "As senhas não coincidem.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tenta criar o usuário no Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Cadastro bem-sucedido, salva os dados no Realtime Database
                        FirebaseUser user = mAuth.getCurrentUser();
                        salvarDadosUsuario(user, nome, cpf, email);

                    } else {
                        // Exibe a mensagem de erro
                        Toast.makeText(CadastroUsuarioActivity.this, "Falha ao criar usuário: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void salvarDadosUsuario(FirebaseUser user, String nome, String cpf, String email) {
        if (user == null) return;

        // Cria um objeto User com o valor de admin igual a 0 (não admin)
        User usuario = new User(nome, cpf, email, 0);

        // Salva os dados do usuário no nó "Users/{userId}"
        databaseReference.child(user.getUid()).setValue(usuario)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        finish(); // Finaliza a activity
                    }
                });
    }
}
