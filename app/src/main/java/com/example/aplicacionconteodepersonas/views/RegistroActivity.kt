package com.example.aplicacionconteodepersonas.views

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aplicacionconteodepersonas.databinding.ActivityRegistroBinding
import com.example.aplicacionconteodepersonas.models.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Pantalla de Registro de nuevos usuarios.
 * Cumple con los Indicadores 2 y 3 de la evaluación:
 * - Validaciones locales antes de enviar a Firebase.
 * - Registro en Firebase Authentication.
 * - Creación de documento en la colección "usuarios" de Firestore.
 */
class RegistroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
    }

    fun onRegistrarClick(view: View) {
        val nombre = binding.edtNombre.text.toString().trim()
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()
        val confirmPassword = binding.edtConfirmPassword.text.toString().trim()

        var hayError = false

        // Validación de nombre
        if (nombre.isEmpty()) {
            binding.edtNombre.error = "Ingresa tu nombre completo"
            hayError = true
        }

        // Validación de email: debe contener formato y dominio válido (ej: usuario@correo.com o .cl)
        if (email.isEmpty()) {
            binding.edtEmail.error = "Ingresa tu email"
            hayError = true
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() || !email.contains(".")) {
            binding.edtEmail.error = "Email inválido o sin dominio (ej: usuario@correo.com)"
            hayError = true
        }

        // Validación de contraseña: mínimo 6 caracteres
        if (password.isEmpty()) {
            binding.edtPassword.error = "Ingresa una contraseña"
            hayError = true
        } else if (password.length < 6) {
            binding.edtPassword.error = "La contraseña debe tener al menos 6 caracteres"
            hayError = true
        }

        // Validación de coincidencia de contraseña
        if (confirmPassword.isEmpty()) {
            binding.edtConfirmPassword.error = "Confirma tu contraseña"
            hayError = true
        } else if (password != confirmPassword) {
            binding.edtConfirmPassword.error = "Las contraseñas no coinciden"
            hayError = true
        }

        // Si hay errores locales, NO se envía nada a Firebase (Indicador 2)
        if (hayError) {
            return
        }

        binding.pbRegistro.visibility = View.VISIBLE
        binding.btnRegistrar.isEnabled = false

        // Registro en Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: ""
                    val fechaActual = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

                    val nuevoUsuario = Usuario(
                        uid = uid,
                        email = email,
                        nombre = nombre,
                        fechaRegistro = fechaActual
                    )

                    // Guardado en la colección "usuarios" de Firestore (Indicador 3)
                    FirebaseFirestore.getInstance()
                        .collection("usuarios")
                        .document(uid)
                        .set(nuevoUsuario)
                        .addOnSuccessListener {
                            binding.pbRegistro.visibility = View.GONE
                            binding.btnRegistrar.isEnabled = true
                            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()

                            // Navega a Bienvenida pasando el email por Intent
                            val intent = Intent(this, BienvenidaActivity::class.java)
                            intent.putExtra("usuario", email)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { e ->
                            binding.pbRegistro.visibility = View.GONE
                            binding.btnRegistrar.isEnabled = true
                            Toast.makeText(this, "Usuario creado pero error al guardar perfil: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                } else {
                    binding.pbRegistro.visibility = View.GONE
                    binding.btnRegistrar.isEnabled = true
                    Toast.makeText(this, "Error al registrar: ${task.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
    }

    fun onVolverLoginClick(view: View) {
        finish()
    }
}
