package com.example.salvadortech;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class ChamadosActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ChamadoAdapter adapter;
    private List<Chamado> chamadosList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chamados); // Certifique-se de que seu layout está correto

        recyclerView = findViewById(R.id.recycler_view_chamados);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        chamadosList = new ArrayList<>();
        adapter = new ChamadoAdapter(chamadosList);
        recyclerView.setAdapter(adapter);

        // Inicializa o Firestore
        db = FirebaseFirestore.getInstance();

        // Busca os serviços do Firestore
        db.collection("Servicos")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getId(); // Obtém o ID do documento
                            String descricao = document.getString("descricao");
                            String observacoes = document.getString("observacoes");
                            String pecas = document.getString("pecas");
                            String status = document.getString("status");

                            chamadosList.add(new Chamado(id, descricao, observacoes, pecas, status));
                        }
                        adapter.notifyDataSetChanged(); // Atualiza o RecyclerView
                    } else {
                        // Trate o erro aqui
                    }
                });
    }
}


