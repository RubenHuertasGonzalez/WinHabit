package com.institutvidreres.winhabit.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.institutvidreres.winhabit.MainActivity
import com.institutvidreres.winhabit.R
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
            startActivity(Intent(this, MainActivity::class.java))
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
                            Toast.makeText(this, "Inicio de sesión correcto", Toast.LENGTH_SHORT).show()

                            // Almacena el correo en SharedPreferences
                            val sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("email", email)
                            editor.apply()

                            // Actualiza el correo en el textViewCorreoPerfil del Navigation Drawer
                            val navHeader = findViewById<NavigationView>(R.id.nav_view).getHeaderView(0)
                            navHeader.findViewById<TextView>(R.id.textViewCorreoPerfil).text = email

                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()  // Cerrar esta actividad
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
