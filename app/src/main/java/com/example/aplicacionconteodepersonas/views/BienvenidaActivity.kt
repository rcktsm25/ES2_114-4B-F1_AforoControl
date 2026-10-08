package com.example.aplicacionconteodepersonas.views

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aplicacionconteodepersonas.databinding.ActivityBienvenidaBinding
import com.google.firebase.auth.FirebaseAuth

/**
 * Pantalla de Bienvenida.
 * Cumple con el Indicador 6 de la evaluación:
 * - Saluda mostrando el email/usuario recibido por Intent.
 * - Presenta el servicio que ofrece el proyecto (AforoControl IoT).
 */
class BienvenidaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBienvenidaBinding
    private var usuario: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBienvenidaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        usuario = intent.getStringExtra("usuario") ?: FirebaseAuth.getInstance().currentUser?.email ?: "Usuario"
        binding.txtBienvenida.text = "Bienvenido,\n$usuario"
    }

    fun onPreferenciasClick(view: View) {
        val intent = Intent(this, PreferenciasActivity::class.java)
        intent.putExtra("usuario", usuario)
        startActivity(intent)
    }

    fun onLecturasClick(view: View) {
        val intent = Intent(this, ListaActivity::class.java)
        startActivity(intent)
    }

    fun onCerrarSesionClick(view: View) {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finishAffinity()
    }
}
