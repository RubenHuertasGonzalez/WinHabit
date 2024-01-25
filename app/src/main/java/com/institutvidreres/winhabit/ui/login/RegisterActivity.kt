package com.institutvidreres.winhabit.ui.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.institutvidreres.winhabit.R
import com.institutvidreres.winhabit.databinding.ActivityAuthBinding
import com.institutvidreres.winhabit.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val TAG = "AuthActivity"
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_register)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val buttonRegister = binding.buttonRegister

        buttonRegister.setOnClickListener {
            val emailField = findViewById<EditText>(R.id.editTextEmail)
            val passwordField = findViewById<EditText>(R.id.editTextPassword)

            val email = emailField.text.toString()
            val password = passwordField.text.toString()
            val auth = FirebaseAuth.getInstance()
            val db = FirebaseFirestore.getInstance()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val userInfo = hashMapOf(
                            "email" to email,
                            "password" to password
                        )
                        if (user != null) {
                            db.collection("users").document(user.uid)
                                .set(userInfo)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "CORRECTO CREADO", Toast.LENGTH_SHORT).show()
                                    Log.d(TAG, "DocumentSnapshot successfully written!")
                                }
                                .addOnFailureListener { e ->
                                    Log.w(TAG, "Error writing document", e)
                                }
                        }
                    } else {
                        Log.w(TAG, "createUserWithEmailAndPassword:failure", task.exception)
                    }
                }
        }

    }
}