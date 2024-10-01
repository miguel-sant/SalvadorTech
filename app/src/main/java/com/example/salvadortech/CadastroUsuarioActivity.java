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

public class CadastroUsuarioActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword, inputRepeatPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_cliente);

        inputEmail = findViewById(R.id.input_email); // EditText para email
        inputPassword = findViewById(R.id.input_password);
        inputRepeatPassword = findViewById(R.id.input_repeat_password);
        Button buttonRegister = findViewById(R.id.button_register);

        // Inicialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificarCadastro();
            }
        });
    }

    private void verificarCadastro() {
        String email = inputEmail.getText().toString().trim();
        String senha = inputPassword.getText().toString().trim();
        String repetirSenha = inputRepeatPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(senha) || TextUtils.isEmpty(repetirSenha)) {
            Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!senha.equals(repetirSenha)) {
            Toast.makeText(this, "As senhas não coincidem.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tenta criar o usuário
        mAuth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Cadastro bem-sucedido
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(CadastroUsuarioActivity.this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();
                        finish(); // Finaliza a activity atual
                    } else {
                        // Exibe a mensagem de erro
                        Toast.makeText(CadastroUsuarioActivity.this, "Falha ao criar usuário: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
