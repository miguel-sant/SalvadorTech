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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
public class EsqueceuSenhaActivity extends AppCompatActivity{

    Button button_redefinir;
    EditText input_email_redefinir;
    FirebaseAuth mAuth;
    String strEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esqueceu_senha);

        button_redefinir = findViewById(R.id.button_redefinir);
        input_email_redefinir = findViewById(R.id.input_email_redefinir);

        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode("pt");
        button_redefinir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strEmail = input_email_redefinir.getText().toString().trim();
                if (!TextUtils.isEmpty(strEmail)){
                    ResetPassword();
                }else {
                    input_email_redefinir.setError("Email não pode ser vazio");
                }
            }
        });

    }
    private void ResetPassword() {
        button_redefinir.setVisibility(View.INVISIBLE);

        mAuth.sendPasswordResetEmail(strEmail)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(EsqueceuSenhaActivity.this, "Reset password link has been sent to your registered Email", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EsqueceuSenhaActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {

                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EsqueceuSenhaActivity.this, "Error :- " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
