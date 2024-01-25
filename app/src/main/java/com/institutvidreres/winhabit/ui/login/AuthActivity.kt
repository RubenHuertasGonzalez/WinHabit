package com.institutvidreres.winhabit.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.institutvidreres.winhabit.MainActivity
import com.institutvidreres.winhabit.R
import com.institutvidreres.winhabit.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val TAG = "AuthActivity"
    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val buttonSignIn = binding.buttonSignIn
        val buttonGoToRegister = binding.buttonGoToRegister

        buttonSignIn.setOnClickListener {
            val emailField = findViewById<EditText>(R.id.editTextEmail)
            val passwordField = findViewById<EditText>(R.id.editTextPassword)

            val email = emailField.text.toString()
            val password = passwordField.text.toString()
            val auth = FirebaseAuth.getInstance()
            val db = FirebaseFirestore.getInstance()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "CORRECTO INICIADO", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                    } else {
                        Log.w(TAG, "signInWithEmailAndPassword:failure", task.exception)
                    }
                }
        }

        buttonGoToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}

