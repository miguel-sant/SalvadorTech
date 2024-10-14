package com.example.salvadortech;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Toast;
import android.util.Log;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class ChamadosActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ChamadoAdapter adapter;
    private List<Chamado> chamadosList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chamados); // Certifique-se de que o layout está correto

        recyclerView = findViewById(R.id.recycler_view_chamados);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        chamadosList = new ArrayList<>();
        adapter = new ChamadoAdapter(chamadosList);
        recyclerView.setAdapter(adapter);

        // Inicializa a referência ao Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("Servicos");

        // Configura o clique no item do RecyclerView
        adapter.setOnItemClickListener(new ChamadoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Chamado chamado) {
                Intent intent = new Intent(ChamadosActivity.this, DetalhamentoServicoActivity.class);
                intent.putExtra("ID_SERVICO", chamado.getId());
                startActivity(intent);
            }
        });

        // Busca os dados dos serviços no Firebase
        buscarChamados();
    }

    private void buscarChamados() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot chamadoSnapshot : dataSnapshot.getChildren()) {
                        Long id = chamadoSnapshot.child("id").getValue(Long.class);
                        String descricao = chamadoSnapshot.child("descricao").getValue(String.class);
                        String status = chamadoSnapshot.child("status").getValue(String.class);
                        String observacoes = chamadoSnapshot.child("observacoes").getValue(String.class);
                        String pecas = chamadoSnapshot.child("pecas").getValue(String.class);

                        Chamado chamado = new Chamado(id, descricao, status, observacoes, pecas);
                        chamadosList.add(chamado);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ChamadosActivity.this, "Nenhum chamado encontrado.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ChamadosActivity", "Erro ao buscar chamados: " + databaseError.getMessage());
                Toast.makeText(ChamadosActivity.this, "Erro ao buscar chamados.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
