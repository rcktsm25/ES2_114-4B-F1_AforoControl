package com.example.aplicacionconteodepersonas.views

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aplicacionconteodepersonas.databinding.ActivityPreferenciasBinding
import com.google.firebase.auth.FirebaseAuth

/**
 * Pantalla de Preferencias del negocio.
 * Cumple con el Indicador 7 de la evaluación:
 * - Switch con opción de negocio: "Activar alerta acústica por sobrecupo".
 * - Spinner con zonas del negocio: "Auditorio", "Biblioteca", "Laboratorio IoT", "Casino Central".
 */
class PreferenciasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPreferenciasBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPreferenciasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Cargar preferencias guardadas si existen
        val prefs = getSharedPreferences("AforoPrefs", Context.MODE_PRIVATE)
        binding.swAlertaSobrecupo.isChecked = prefs.getBoolean("alerta_sobrecupo", true)
        val zonaIndex = prefs.getInt("zona_index", 0)
        if (zonaIndex < binding.spZonaMonitoreada.count) {
            binding.spZonaMonitoreada.setSelection(zonaIndex)
        }
    }

    fun onGuardarPreferenciaClick(view: View) {
        val alertaActiva = binding.swAlertaSobrecupo.isChecked
        val zonaSeleccionada = binding.spZonaMonitoreada.selectedItem.toString()
        val zonaPos = binding.spZonaMonitoreada.selectedItemPosition
        val umbral = if (binding.rbAlerta80.isChecked) "80%" else "100%"

        binding.pbGuardando.visibility = View.VISIBLE
        binding.btnGuardarPreferencia.isEnabled = false

        Handler(Looper.getMainLooper()).postDelayed({
            // Guardar en SharedPreferences
            val prefs = getSharedPreferences("AforoPrefs", Context.MODE_PRIVATE)
            prefs.edit()
                .putBoolean("alerta_sobrecupo", alertaActiva)
                .putInt("zona_index", zonaPos)
                .putString("umbral", umbral)
                .apply()

            binding.pbGuardando.visibility = View.GONE
            binding.btnGuardarPreferencia.isEnabled = true

            val estadoAlerta = if (alertaActiva) "activada" else "desactivada"
            Toast.makeText(
                this,
                "Preferencias guardadas: Alerta $estadoAlerta en $zonaSeleccionada (Umbral $umbral)",
                Toast.LENGTH_LONG
            ).show()
        }, 800)
    }

    fun onVolverClick(view: View) {
        finish()
    }

    fun onCerrarSesionClick(view: View) {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finishAffinity()
    }
}
