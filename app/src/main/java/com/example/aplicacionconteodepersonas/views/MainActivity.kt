package com.example.aplicacionconteodepersonas.views

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aplicacionconteodepersonas.R
import com.example.aplicacionconteodepersonas.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider

/**
 * Pantalla de inicio de sesión (Login).
 * Cumple con los Indicadores 1, 4, 5 y 6 de la evaluación.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private var mostrandoPassword = false
    private var intentosFallidos = 0

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                binding.pbLogin.visibility = View.GONE
                binding.btnGoogle.isEnabled = true
                Toast.makeText(this, "Google Sign-In cancelado: ${e.statusCode}", Toast.LENGTH_SHORT).show()
            }
        } else {
            binding.pbLogin.visibility = View.GONE
            binding.btnGoogle.isEnabled = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()

        // Configuración de Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    fun onIngresarClick(view: View) {
        val usuario = binding.edtUsuario.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()

        if (usuario.isEmpty() || password.isEmpty()) {
            intentosFallidos++
            Toast.makeText(this, "Completa usuario y contraseña (intento $intentosFallidos)", Toast.LENGTH_SHORT).show()
            return
        }

        // Indicador 4: Validación de formato de email con "@"
        if (!usuario.contains("@")) {
            binding.edtUsuario.error = "Ingresa un email válido (debe contener '@')"
            return
        }

        if (usuario.length > 60) {
            binding.edtUsuario.error = "El correo no puede tener más de 60 caracteres"
            return
        }

        if (password.length < 6) {
            binding.edtPassword.error = "La contraseña debe tener al menos 6 caracteres"
            return
        }

        if (password.length > 30) {
            binding.edtPassword.error = "La contraseña no puede tener más de 30 caracteres"
            return
        }

        binding.pbLogin.visibility = View.VISIBLE
        binding.btnIngresar.isEnabled = false

        // Autenticación con Firebase
        auth.signInWithEmailAndPassword(usuario, password)
            .addOnCompleteListener(this) { task ->
                binding.pbLogin.visibility = View.GONE
                binding.btnIngresar.isEnabled = true

                if (task.isSuccessful) {
                    // Indicador 6: Mensaje de éxito y pasar a Bienvenida con el email recibido por Intent
                    Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                    val email = auth.currentUser?.email ?: usuario
                    abrirBienvenida(email)
                } else {
                    intentosFallidos++
                    // Indicador 5: Mostrar mensaje de error de Firebase y quedarse en Login
                    val mensajeError = when (task.exception) {
                        is FirebaseAuthInvalidUserException ->
                            "No existe una cuenta registrada con este correo"
                        is FirebaseAuthInvalidCredentialsException ->
                            "Correo o contraseña incorrectos"
                        else ->
                            task.exception?.localizedMessage ?: "Error al iniciar sesión"
                    }
                    Toast.makeText(this, "$mensajeError (intento $intentosFallidos)", Toast.LENGTH_LONG).show()
                }
            }
    }

    fun onIrRegistroClick(view: View) {
        val intent = Intent(this, RegistroActivity::class.java)
        startActivity(intent)
    }

    fun onGoogleSignInClick(view: View) {
        binding.pbLogin.visibility = View.VISIBLE
        binding.btnGoogle.isEnabled = true
        googleSignInClient.signOut().addOnCompleteListener {
            val signInIntent = googleSignInClient.signInIntent
            googleSignInLauncher.launch(signInIntent)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                binding.pbLogin.visibility = View.GONE
                binding.btnGoogle.isEnabled = true
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val email = user?.email ?: user?.displayName ?: "Usuario"
                    Toast.makeText(this, "Bienvenido con Google", Toast.LENGTH_SHORT).show()
                    abrirBienvenida(email)
                } else {
                    Toast.makeText(
                        this,
                        "Error al autenticar con Google: ${task.exception?.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun abrirBienvenida(usuario: String) {
        val intent = Intent(this, BienvenidaActivity::class.java)
        intent.putExtra("usuario", usuario)
        startActivity(intent)
    }

    fun onLimpiarClick(view: View) {
        binding.edtUsuario.setText("")
        binding.edtPassword.setText("")
        binding.edtUsuario.error = null
        binding.edtPassword.error = null
        binding.chkRecordarme.isChecked = false
    }

    fun onMostrarPasswordClick(view: View) {
        mostrandoPassword = !mostrandoPassword
        if (mostrandoPassword) {
            binding.edtPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            binding.edtPassword.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        binding.edtPassword.setSelection(binding.edtPassword.text.length)
    }
}
