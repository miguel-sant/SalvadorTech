package com.example.salvadortech;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private EditText edt_email;
    private EditText edt_senha;
    private Button btn_login;
    private TextView btn_cadastrar;
    private FirebaseAuth mAuth;
    private TextView btn_esqueceuSenha;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        edt_email = findViewById(R.id.username);
        edt_senha = findViewById(R.id.password);
        btn_login = findViewById(R.id.button_login);
        btn_cadastrar = findViewById(R.id.btn_signUp);
        btn_esqueceuSenha = findViewById(R.id.btn_esqueceuSenha);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loginEmail = edt_email.getText().toString().trim(); // Remover espaços em branco
                String loginSenha = edt_senha.getText().toString().trim();

                if (!TextUtils.isEmpty(loginEmail) && !TextUtils.isEmpty(loginSenha)) {
                    mAuth.signInWithEmailAndPassword(loginEmail, loginSenha)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        abrirTelaPrincipal(); // Chama método para abrir a tela principal
                                    } else {
                                    }
                                    String error = task.getException() != null ? task.getException().getMessage() : "Erro desconhecido";
                                    Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(LoginActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirTelaCadastro();
            }
        });

        btn_esqueceuSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirTelaEsqueceuSenha();
            }
        });
    }

    private void abrirTelaPrincipal() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void abrirTelaCadastro() {
        Intent intent = new Intent(LoginActivity.this, CadastroUsuarioActivity.class);
        startActivity(intent);
    }

    private void abrirTelaEsqueceuSenha() {
        Intent intent = new Intent(LoginActivity.this, EsqueceuSenhaActivity.class);
        startActivity(intent);
    }
}
