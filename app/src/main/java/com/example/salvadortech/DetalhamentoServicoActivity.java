package com.example.salvadortech;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

public class DetalhamentoServicoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Associa o layout XML à Activity
        setContentView(R.layout.activity_detalhamento);

        // Acessa os TextViews definidos no XML
        TextView idServico = findViewById(R.id.id_servico);
        TextView descricao = findViewById(R.id.descricao);
        TextView status = findViewById(R.id.status);
        TextView observacao = findViewById(R.id.observacao);
        TextView pecas = findViewById(R.id.pecas);

        // Opcional: Você pode modificar o conteúdo dos TextViews aqui, se necessário
        idServico.setText("Serviço: 123456"); // Exemplo de mudança de texto
    }
}
