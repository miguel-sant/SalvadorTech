package com.example.salvadortech;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SalvadorTechActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salvador_tech);

        TextView noAccountText = findViewById(R.id.noAccountText);

        // Adicionando sublinhado ao texto
        noAccountText.setText(Html.fromHtml("<u>Não tenho uma conta</u>"));

        // Adicionando evento de clique
        noAccountText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar para a tela de cadastro (substitua RegisterActivity pelo nome da sua Activity)
                Intent intent = new Intent(SalvadorTechActivity.this, CadastroUsuarioActivity.class);
                startActivity(intent);
            }
        });

        // Para permitir que o TextView trate links, se necessário
        noAccountText.setMovementMethod(LinkMovementMethod.getInstance());
    }
}

