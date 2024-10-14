package com.example.salvadortech

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.widget.PopupMenu
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HomeActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var databaseReference: DatabaseReference
    private lateinit var servicosLayout: LinearLayout

    // Variável para armazenar o status de admin
    private var isAdmin: Boolean = false

    // Firebase Auth para capturar o usuário autenticado
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        drawerLayout = findViewById(R.id.drawer_layout)

        // Inicializa a referência do Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("Servicos")
        servicosLayout = findViewById(R.id.card_servico) // Layout onde os cards serão adicionados

        // Inicializa o Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Chama o método para carregar serviços
        carregarServicos()

        // Configura o ícone do menu para abrir o DrawerLayout
        val menuIcon: ImageView = findViewById(R.id.menu_icon)
        menuIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Configurar cliques para as opções do menu
        val addServiceOption: TextView = findViewById(R.id.add_service_option)
        addServiceOption.setOnClickListener {
            val intent = Intent(this, AddServicoActivity::class.java)
            startActivity(intent)
        }

        val cadastrarFunc: TextView = findViewById(R.id.cadastrar_funcionario)
        cadastrarFunc.setOnClickListener {
            val intent = Intent(this, CadastroFuncionarioActivity::class.java)
            startActivity(intent)
        }

        val faq: ImageView = findViewById(R.id.faq)
        faq.setOnClickListener {
            val intent = Intent(this, FaqActivity::class.java)
            startActivity(intent)
        }

        val chamados: TextView = findViewById(R.id.chamados)
        chamados.setOnClickListener {
            val intent = Intent(this, ChamadosActivity::class.java)
            startActivity(intent)
        }
            // Verifica se o usuário é admin ao carregar a tela
        verificarAdminStatus()
    }


    fun showProfileMenu(view: View) {
        val popup = PopupMenu(this, view)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.menu_profile, popup.menu)
        popup.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {

                R.id.action_logout -> {
                    logout()
                    true
                }

                else -> false
            }
        }
        popup.show()
    }
        private fun logout() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
        private fun verificarAdminStatus() {
        // Obtém o usuário atual
        val currentUser = auth.currentUser
        currentUser?.let {
            val userId = it.uid

            val userReference = FirebaseDatabase.getInstance().getReference("Users/$userId")
            userReference.child("admin").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Verifica se o valor é 1 ou 0
                        val adminValue = dataSnapshot.getValue(Int::class.java) ?: 0
                        isAdmin = adminValue == 1 // Verifica se o valor é 1
                        atualizarVisibilidadeMenu()

                        // Chama carregarServicos aqui, após definir isAdmin
                        carregarServicos() // Chamada para carregar os serviços com base no status de admin
                    } else {
                        Toast.makeText(this@HomeActivity, "Usuário não encontrado.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(this@HomeActivity, "Erro ao verificar status de administrador.", Toast.LENGTH_SHORT).show()
                }
            })
        } ?: run {
            Toast.makeText(this, "Usuário não autenticado.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun atualizarVisibilidadeMenu() {

        val addServiceOption: TextView = findViewById(R.id.add_service_option)
        val cadastrarFunc: TextView = findViewById(R.id.cadastrar_funcionario)
        val chamados: TextView = findViewById(R.id.chamados)

        if (isAdmin) {
            addServiceOption.visibility = View.VISIBLE
            cadastrarFunc.visibility = View.VISIBLE
            chamados.visibility = View.VISIBLE
        } else {
            addServiceOption.visibility = View.GONE
            cadastrarFunc.visibility = View.GONE
            chamados.visibility = View.GONE
        }

        Log.d("HomeActivity", "isAdmin: $isAdmin")
    }
    private fun getUserCpf(userId: String, callback: (String?) -> Unit) {
        val userReference = FirebaseDatabase.getInstance().getReference("Users/$userId")
        userReference.child("cpf").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val cpf = dataSnapshot.getValue(String::class.java)
                    callback(cpf)
                } else {
                    callback(null)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@HomeActivity, "Erro ao obter o CPF do usuário.", Toast.LENGTH_SHORT).show()
                callback(null)
            }
        })
    }

    private fun carregarServicos() {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val userId = user.uid
            getUserCpf(userId) { userCpf ->
                if (userCpf != null) {
                    val userReference = FirebaseDatabase.getInstance().getReference("Users/$userId")

                    // Verifica se o usuário é um administrador
                    userReference.child("admin").addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()) {
                                val adminValue = dataSnapshot.getValue(Int::class.java) ?: 0
                                val isAdmin = adminValue == 1
                                atualizarVisibilidadeMenu() // Atualiza a visibilidade do menu

                                if (isAdmin) {
                                    // Carrega os serviços para o administrador
                                    val query = databaseReference.orderByChild("id").limitToLast(5)
                                    query.addValueEventListener(object : ValueEventListener {
                                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                                            servicosLayout.removeAllViews()
                                            if (dataSnapshot.exists()) {
                                                val servicosList = mutableListOf<Servico>()
                                                for (servicoSnapshot in dataSnapshot.children) {
                                                    val servico = servicoSnapshot.getValue(Servico::class.java)
                                                    servico?.let { servicosList.add(it) }
                                                }
                                                servicosList.sortByDescending { it.getId() }
                                                for (servico in servicosList) {
                                                    criarCardServico(servico)
                                                }
                                            } else {
                                                mostrarMensagemSemServicos()
                                            }
                                        }

                                        override fun onCancelled(databaseError: DatabaseError) {
                                            Toast.makeText(this@HomeActivity, "Erro ao carregar serviços.", Toast.LENGTH_SHORT).show()
                                        }
                                    })
                                } else {
                                    // Carrega os serviços para usuários não administradores
                                    val userServicosQuery = databaseReference.orderByChild("cpfUser").equalTo(userCpf).limitToLast(5)
                                    userServicosQuery.addValueEventListener(object : ValueEventListener {
                                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                                            servicosLayout.removeAllViews()
                                            if (dataSnapshot.exists()) {
                                                val servicosList = mutableListOf<Servico>()
                                                for (servicoSnapshot in dataSnapshot.children) {
                                                    val servico = servicoSnapshot.getValue(Servico::class.java)
                                                    servico?.let { servicosList.add(it) }
                                                }
                                                servicosList.sortByDescending { it.getId() }
                                                for (servico in servicosList) {
                                                    criarCardServico(servico)
                                                }
                                            } else {
                                                mostrarMensagemSemServicos()
                                            }
                                        }

                                        override fun onCancelled(databaseError: DatabaseError) {
                                            Toast.makeText(this@HomeActivity, "Erro ao carregar serviços.", Toast.LENGTH_SHORT).show()
                                        }
                                    })
                                }
                            } else {
                                Toast.makeText(this@HomeActivity, "Usuário não encontrado.", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Toast.makeText(this@HomeActivity, "Erro ao verificar status de administrador.", Toast.LENGTH_SHORT).show()
                        }
                    })
                } else {
                    Toast.makeText(this@HomeActivity, "Erro ao obter CPF do usuário.", Toast.LENGTH_SHORT).show()
                }
            }
        } ?: run {
            Toast.makeText(this, "Usuário não autenticado.", Toast.LENGTH_SHORT).show()
        }
    }





    private fun mostrarMensagemSemServicos() {
        val mensagemSemServicos = TextView(this@HomeActivity)
        mensagemSemServicos.text = "Nenhum serviço cadastrado"
        mensagemSemServicos.textSize = 18f
        mensagemSemServicos.textAlignment = View.TEXT_ALIGNMENT_CENTER
        servicosLayout.addView(mensagemSemServicos)
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