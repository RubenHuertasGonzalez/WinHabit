package com.institutvidreres.winhabit.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.institutvidreres.winhabit.MainActivity
import com.institutvidreres.winhabit.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val TAG = "AuthActivity"
    private lateinit var binding: ActivityAuthBinding

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Usuario ya autenticado, redirigir a la actividad principal
            startActivity(Intent(this, MainActivity::class.java)
                .putExtra("user_email", currentUser.email))
            finish()  // Cerrar esta actividad para evitar que el usuario retroceda a la pantalla de inicio de sesión
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val buttonLogin = binding.buttonSignIn

        val buttonGoRegister = binding.buttonGoToRegister

        buttonLogin.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Inicio de sesión exitoso, redirigir a la actividad principal
                        val user = auth.currentUser
                        if (user != null) {
                            // Obtener el ID del personaje del usuario desde Firestore
                            val db = FirebaseFirestore.getInstance()
                            db.collection("users").document(user.uid)
                                .get()
                                .addOnSuccessListener { document ->
                                    val characterId = document.getLong("character")
                                    if (characterId != null) {
                                        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                                        sharedPreferences.edit().putInt("user_character", characterId.toInt()).apply()
                                        startActivity(Intent(this, MainActivity::class.java)
                                            .putExtra("user_email", user.email)
                                            .putExtra("user_character", characterId.toInt()))
                                        finish()  // Cerrar esta actividad
                                    } else {
                                        Log.e(TAG, "Error: No se encontró el ID del personaje para el usuario")
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Log.e(TAG, "Error al obtener el ID del personaje del usuario", e)
                                }
                        }
                    } else {
                        // Fallo en el inicio de sesión
                        Log.w(TAG, "signInWithEmailAndPassword:failure", task.exception)
                        Toast.makeText(this, "Error en el inicio de sesión", Toast.LENGTH_SHORT).show()
                    }
                }

        }

        buttonGoRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
