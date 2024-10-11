package com.example.salvadortech

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout

import android.view.LayoutInflater
import android.view.View
import com.google.firebase.database.*


class HomeActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var databaseReference: DatabaseReference
    private lateinit var servicosLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        drawerLayout = findViewById(R.id.drawer_layout)

        // Inicializa a referência do Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("Servicos")
        servicosLayout = findViewById(R.id.card_servico) // Layout onde os cards serão adicionados

        // Chama o método para carregar serviços
        carregarServicos()

        // Configura o ícone do menu para abrir o DrawerLayout
        val menuIcon: ImageView = findViewById(R.id.menu_icon)
        menuIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Configurar o clique para abrir outra Activity ao clicar no TextView "Adicionar Serviço"
        val addServiceOption: TextView = findViewById(R.id.add_service_option)
        addServiceOption.setOnClickListener {
            val intent = Intent(this, AddServicoActivity::class.java)
            startActivity(intent)
        }
    }

    private fun carregarServicos() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Limpar os cards existentes para evitar duplicatas
                servicosLayout.removeAllViews()

                if (dataSnapshot.exists()) {
                    // Se existem serviços, percorre o snapshot e cria os cards
                    for (servicoSnapshot in dataSnapshot.children) {
                        val servico = servicoSnapshot.getValue(Servico::class.java)
                        servico?.let {
                            criarCardServico(it)
                        }
                    }
                } else {
                    // Se não existem serviços, mostrar a mensagem "Nenhum serviço cadastrado"
                    val mensagemSemServicos = TextView(this@HomeActivity)
                    mensagemSemServicos.text = "Nenhum serviço cadastrado"
                    mensagemSemServicos.textSize = 18f
                    mensagemSemServicos.textAlignment = View.TEXT_ALIGNMENT_CENTER
                    servicosLayout.addView(mensagemSemServicos)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@HomeActivity, "Erro ao carregar serviços.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun criarCardServico(servico: Servico) {
        // Inflar o layout do card
        val inflater = LayoutInflater.from(this)
        val cardView: View = inflater.inflate(R.layout.card_servico, servicosLayout, false)

        // Configurar os dados no card
        val titleTextView = cardView.findViewById<TextView>(R.id.card_title)
        val statusTextView = cardView.findViewById<TextView>(R.id.card_status)

        titleTextView.text = servico.getDescricao() // Defina o título do card
        statusTextView.text = servico.getStatus() // Defina o status do card

        // Configurar clique no card
        cardView.setOnClickListener {
            val intent = Intent(this, DetalhamentoServicoActivity::class.java)
            // Passar dados para a próxima atividade
            intent.putExtra("ID_SERVICO", servico.getId()) // Agora você pode usar getId()
            startActivity(intent)
        }

        // Adicionar o card à LinearLayout
        servicosLayout.addView(cardView)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
