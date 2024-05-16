package com.institutvidreres.winhabit.ui.login

import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.institutvidreres.winhabit.MainActivity
import com.institutvidreres.winhabit.R
import com.institutvidreres.winhabit.databinding.ActivityRegisterBinding
import com.institutvidreres.winhabit.utils.AppUtils
import com.institutvidreres.winhabit.utils.ConnectivityReceiverRegister

class RegisterActivity : AppCompatActivity() {
    //TODO: Correo de confirmación de cuenta

    private lateinit var auth: FirebaseAuth
    private val TAG = "AuthActivity"
    private lateinit var binding: ActivityRegisterBinding
    private var selectedCharacter: Int = -1
    private lateinit var storageReference: StorageReference
    private lateinit var progressBar: View
    private lateinit var connectivityReceiverRegister: ConnectivityReceiverRegister

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        // Get references to image views
        val imageViewVaquero = binding.imageViewVaquero
        val imageViewMago = binding.imageViewMago
        val imageViewArquero = binding.imageViewArquero
        val imageViewVaquera = binding.imageViewVaquera
        val imageViewBruja = binding.imageViewBruja
        val imageViewArquera = binding.imageViewArquera

        // Set click listeners for character selection
        setCharacterClickListener(imageViewVaquero, 0)
        setCharacterClickListener(imageViewMago, 1)
        setCharacterClickListener(imageViewArquero, 2)
        setCharacterClickListener(imageViewVaquera, 3)
        setCharacterClickListener(imageViewBruja, 4)
        setCharacterClickListener(imageViewArquera, 5)

        val buttonRegister = binding.buttonRegister
        val buttonGoLogin = binding.buttonGoToLogin
        progressBar = binding.progressBar

        // Inicializar el receptor de conectividad
        connectivityReceiverRegister = ConnectivityReceiverRegister(this)

        // Verificar la conectividad al inicio
        checkConnectivity()

        buttonRegister.setOnClickListener {
            if (AppUtils.isInternetConnected(this)) {

                // Resto de tu código para registrar al usuario
                val email = binding.editTextEmail.text.toString()
                val password = binding.editTextPassword.text.toString()
                val username = binding.editTextUsername.text.toString()

                if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // Check if password has at least 6 characters
                if (password.length < 6) {
                    Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // Check if a character is selected
                if (selectedCharacter == -1) {
                    Toast.makeText(this, "Por favor, selecciona un personaje", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val auth = FirebaseAuth.getInstance()
                val db = FirebaseFirestore.getInstance()

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            val userId = user?.uid
                            val userInfo = hashMapOf(
                                "email" to email,
                                "username" to username,
                                "password" to password,
                                "character" to selectedCharacter,
                                "banner" to 19
                            )
                            if (user != null) {
                                db.collection("users").document(user.uid)
                                    .set(userInfo)
                                    .addOnSuccessListener {
                                        val successMessage = "Bienvenido a WinHabit: $email"
                                        Toast.makeText(this, successMessage, Toast.LENGTH_SHORT).show()
                                        Log.d(TAG, "DocumentSnapshot successfully written!")
                                        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                                        sharedPreferences.edit().putInt("user_character", selectedCharacter).apply()
                                        sharedPreferences.edit().putString("user_name", username).apply()
                                        val intent = Intent(this, MainActivity::class.java)
                                        intent.putExtra("user_email", email)
                                        intent.putExtra("user_name", username)
                                        intent.putExtra("user_character", selectedCharacter)
                                        startActivity(intent)
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w(TAG, "Error writing document", e)
                                    }
                            }
                        } else {
                            Log.w(TAG, "createUserWithEmailAndPassword:failure", task.exception)
                        }
                    }
            } else {
                Toast.makeText(this, "No hay conexión a Internet", Toast.LENGTH_SHORT).show()
            }
        }

        buttonGoLogin.setOnClickListener{
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setCharacterClickListener(imageView: ImageView, characterIndex: Int) {
        imageView.setOnClickListener {
            // Deseleccionar el personaje previamente seleccionado (si hay alguno)
            if (selectedCharacter != -1) {
                val previousSelectedImageView = getImageViewByCharacterIndex(selectedCharacter)
                previousSelectedImageView.setBackgroundResource(R.drawable.image_border)
            }

            // Seleccionar el nuevo personaje
            selectedCharacter = characterIndex
            // Cambiar la apariencia del personaje seleccionado
            imageView.setBackgroundResource(R.drawable.selected_image_border)
        }
    }

    private fun getImageViewByCharacterIndex(characterIndex: Int): ImageView {
        return when (characterIndex) {
            0 -> binding.imageViewVaquero
            1 -> binding.imageViewMago
            2 -> binding.imageViewArquero
            3 -> binding.imageViewVaquera
            4 -> binding.imageViewBruja
            5 -> binding.imageViewArquera
            else -> throw IllegalArgumentException("Índice de personaje no válido")
        }
    }

    override fun onResume() {
        super.onResume()
        // Registrar el receptor de conectividad
        registerReceiver(connectivityReceiverRegister, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onPause() {
        super.onPause()
        // Desregistrar el receptor de conectividad
        unregisterReceiver(connectivityReceiverRegister)
    }

    // Método para verificar la conectividad
    private fun checkConnectivity() {
        val isConnected = AppUtils.isInternetConnected(this)
        updateUI(isConnected)
    }

    // Método para actualizar la interfaz de usuario según el estado de la conectividad
    fun updateUI(isConnected: Boolean) {
        if (!isConnected) {
            // No hay conexión a Internet
            Toast.makeText(this, "Error de Conexión", Toast.LENGTH_SHORT).show()
            progressBar.visibility = View.VISIBLE
        } else {
            // Hay conexión a Internet
            progressBar.visibility = View.GONE
        }
    }
}