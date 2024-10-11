package com.example.salvadortech;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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

        // Busca os dados dos serviços no Firebase
        buscarChamados();
    }

    private void buscarChamados() {
        // Faz a consulta para buscar todos os chamados
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Verifica se há chamados disponíveis
                if (dataSnapshot.exists()) {
                    for (DataSnapshot chamadoSnapshot : dataSnapshot.getChildren()) {
                        // Converte os dados para o modelo de chamado
                        Long id = chamadoSnapshot.child("id").getValue(Long.class); // Use Long aqui
                        String descricao = chamadoSnapshot.child("descricao").getValue(String.class);
                        String status = chamadoSnapshot.child("status").getValue(String.class);
                        String observacoes = chamadoSnapshot.child("observacoes").getValue(String.class);
                        String pecas = chamadoSnapshot.child("pecas").getValue(String.class);

                        // Adiciona o chamado à lista
                        Chamado chamado = new Chamado(id, descricao, status, observacoes, pecas); // Certifique-se de que a ordem está correta
                        chamadosList.add(chamado);
                    }
                    adapter.notifyDataSetChanged(); // Atualiza o RecyclerView
                } else {
                    Toast.makeText(ChamadosActivity.this, "Nenhum chamado encontrado.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Em caso de erro
                Log.e("ChamadosActivity", "Erro ao buscar chamados: " + databaseError.getMessage());
                Toast.makeText(ChamadosActivity.this, "Erro ao buscar chamados.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
