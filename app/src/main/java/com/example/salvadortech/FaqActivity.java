package com.example.salvadortech;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class FaqActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        ImageView menuIcon = findViewById(R.id.menu_icon);

        // Adiciona um listener de clique no botão
        menuIcon.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Inicia a atividade da tela inicial
                Intent intent = new Intent(FaqActivity.this, HomeActivity.class); // Substitua HomeActivity pela sua atividade de home
                startActivity(intent);
                finish(); // Finaliza a atividade atual para que o usuário não possa voltar a ela pressionando o botão Voltar
            }
        });
    }
}
