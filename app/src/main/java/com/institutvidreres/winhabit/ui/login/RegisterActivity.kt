package com.institutvidreres.winhabit.ui.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
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

class RegisterActivity : AppCompatActivity() {
    //TODO: Correo de confirmación de cuenta

    private lateinit var auth: FirebaseAuth
    private val TAG = "AuthActivity"
    private lateinit var binding: ActivityRegisterBinding
    private var selectedCharacter: Int = -1
    private lateinit var storageReference: StorageReference

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

        buttonRegister.setOnClickListener {
            // Check if a character is selected
            if (selectedCharacter == -1) {
                Toast.makeText(this, "Por favor, selecciona un personaje", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Resto de tu código para registrar al usuario
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()

            val auth = FirebaseAuth.getInstance()
            val db = FirebaseFirestore.getInstance()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val userInfo = hashMapOf(
                            "email" to email,
                            "password" to password,
                            "character" to selectedCharacter
                        )
                        if (user != null) {
                            db.collection("users").document(user.uid)
                                .set(userInfo)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "CORRECTO CREADO", Toast.LENGTH_SHORT).show()
                                    Log.d(TAG, "DocumentSnapshot successfully written!")
                                    val intent = Intent(this, MainActivity::class.java)
                                    intent.putExtra("user_email", email)
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

            storageReference.child("${auth.currentUser?.uid}/profile_image.png")
                .putFile(getImageResourceUri(selectedCharacter))
                .addOnSuccessListener { _ ->
                    // Imagen del personaje almacenada exitosamente
                }
                .addOnFailureListener { e ->
                    // Maneja el error al almacenar la imagen del personaje
                    Log.e(TAG, "Error al almacenar la imagen del personaje", e)
                }
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

    private fun getImageResourceUri(characterIndex: Int): Uri {
        val imageName = when (characterIndex) {
            0 -> "vaquero.png"
            1 -> "mago.png"
            2 -> "arquero.png"
            3 -> "vaquera.png"
            4 -> "bruja.png"
            5 -> "arquera.png"
            else -> throw IllegalArgumentException("Índice de personaje no válido")
        }

        return Uri.parse("android.resource://${packageName}/drawable/${imageName}")
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
}