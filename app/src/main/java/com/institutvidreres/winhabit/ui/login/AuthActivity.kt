package com.institutvidreres.winhabit.ui.login

import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.institutvidreres.winhabit.MainActivity
import com.institutvidreres.winhabit.R
import com.institutvidreres.winhabit.databinding.ActivityAuthBinding
import com.institutvidreres.winhabit.utils.AppUtils
import com.institutvidreres.winhabit.utils.ConnectivityReceiverAuth

class AuthActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleApiClient: GoogleApiClient
    private val TAG = "AuthActivityLogs"
    private lateinit var binding: ActivityAuthBinding
    private lateinit var progressBar: View
    private lateinit var connectivityReceiverAuth: ConnectivityReceiverAuth
    private var selectedCharacter: Int = 0

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Usuario ya autenticado, redirigir a la actividad principal
            startActivity(Intent(this, MainActivity::class.java)
                .putExtra("user_email", currentUser.email))
            finish()  // Cerrar esta actividad para evitar que el usuario retroceda a la pantalla de inicio de sesión
        } else {
            // Si el usuario no está autenticado, inicia sesión con el banner por defecto
            val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            sharedPreferences.edit().putInt("user_banner", 19).apply()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        progressBar = binding.progressBar

        val buttonLogin = binding.buttonSignIn
        val buttonGoRegister = binding.buttonGoToRegister

        // Configurar opciones de inicio de sesión con Google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this, this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()

        // Inicializar el receptor de conectividad
        connectivityReceiverAuth = ConnectivityReceiverAuth(this)

        // Verificar la conectividad al inicio
        checkConnectivity()

        buttonLogin.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()

            // Verificar la conectividad antes de iniciar sesión
            if (AppUtils.isInternetConnected(this)) {
                progressBar.visibility = View.VISIBLE
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
                                            progressBar.visibility = View.GONE // Ocultar progressBar
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
                                        progressBar.visibility = View.GONE // Ocultar progressBar
                                    }
                            }
                        } else { // Fallo en el inicio de sesión
                            Log.w(TAG, "signInWithEmailAndPassword:failure", task.exception)
                            Toast.makeText(this, "Correo o contraseña incorrecto", Toast.LENGTH_SHORT).show()
                            progressBar.visibility = View.GONE // Ocultar progressBar
                        }
                    }
            } else {
                // No hay conexión a Internet
                Toast.makeText(this, "No hay conexión a Internet", Toast.LENGTH_SHORT).show()
            }
        }

        buttonGoRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Configurar el clic del botón de inicio de sesión con Google
        binding.btnSignInGoogle.setOnClickListener {
            val signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val result = data?.let { Auth.GoogleSignInApi.getSignInResultFromIntent(it) }
            if (result != null) {
                if (result.isSuccess) {
                    val account = result?.signInAccount
                    firebaseAuthWithGoogle(account)
                } else {
                    // Fallo en el inicio de sesión con Google
                    Log.e(TAG, "Google Sign-In failed.")
                }
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val db = FirebaseFirestore.getInstance()
                    val userInfo = hashMapOf(
                        "email" to account?.email,
                        "username" to account?.displayName,
                        "character" to selectedCharacter,
                    )
                    if (user != null) {
                        db.collection("users").document(user.uid)
                            .set(userInfo)
                            .addOnSuccessListener {
                                Toast.makeText(this, "CORRECTO CREADO", Toast.LENGTH_SHORT).show()
                                Log.d(TAG, "DocumentSnapshot successfully written!")
                                val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                                sharedPreferences.edit().putInt("user_character", selectedCharacter).apply()
                                val intent = Intent(this, MainActivity::class.java)
                                if (account != null) {
                                    intent.putExtra("user_email", account.email)
                                }
                                intent.putExtra("user_character", selectedCharacter)
                                startActivity(intent)
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error writing document", e)
                            }
                    }
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }



    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.d(TAG, "onConnectionFailed: ${connectionResult.errorMessage}")
        Toast.makeText(this@AuthActivity, "Google Play Services error.", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        // Registrar el receptor de conectividad
        registerReceiver(connectivityReceiverAuth, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onPause() {
        super.onPause()
        // Desregistrar el receptor de conectividad
        unregisterReceiver(connectivityReceiverAuth)
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

    companion object {
        private const val RC_SIGN_IN = 9001
    }
}
