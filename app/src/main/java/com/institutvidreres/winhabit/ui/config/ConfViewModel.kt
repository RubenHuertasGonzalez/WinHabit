package com.institutvidreres.winhabit.ui.config

import android.content.ContentValues.TAG
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class ConfViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // LiveData para mensajes de retroalimentación
    private val _messageLiveData = MutableLiveData<String>()
    val messageLiveData: LiveData<String>
        get() = _messageLiveData

    fun changePassword(currentPassword: String, newPassword: String, confirmPassword: String) {
        val user = auth.currentUser

        // Verificar si el usuario actual ha iniciado sesión
        if (user != null) {
            // Reautenticar al usuario para verificar la contraseña actual antes de cambiarla
            val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
            user.reauthenticate(credential).addOnCompleteListener { reAuthTask ->
                if (reAuthTask.isSuccessful) {
                    // Verificar si las nuevas contraseñas coinciden
                    if (newPassword == confirmPassword) {
                        // Actualizar la contraseña en Firebase Auth
                        user.updatePassword(newPassword).addOnCompleteListener { updatePasswordTask ->
                            if (updatePasswordTask.isSuccessful) {
                                Log.d(TAG, "User password updated in Firebase Auth.")

                                // Si la actualización en Firebase Auth fue exitosa, actualiza la contraseña en Firestore
                                updatePasswordInFirestore(user, newPassword)

                                showMessage("Contraseña actualizada correctamente.")
                            } else {
                                // Mostrar mensaje de error si no se pudo actualizar la contraseña
                                Log.e(TAG, "Error updating password in Firebase Auth: ${updatePasswordTask.exception}")
                                showMessage("Error al actualizar la contraseña.")
                            }
                        }
                    } else {
                        // Mostrar mensaje de error si las contraseñas no coinciden
                        Log.e(TAG, "New passwords do not match.")
                        showMessage("Las nuevas contraseñas no coinciden.")
                    }
                } else {
                    // Mostrar mensaje de error si no se pudo reautenticar al usuario
                    Log.e(TAG, "Error reauthenticating user: ${reAuthTask.exception}")
                    showMessage("Error al autenticar al usuario.")
                }
            }
        } else {
            // Mostrar mensaje de error si el usuario no está autenticado
            Log.e(TAG, "User not authenticated.")
            showMessage("Por favor, inicie sesión primero.")
        }
    }

    private fun updatePasswordInFirestore(user: FirebaseUser, newPassword: String) {
        val passwordRef = db.collection("users").document(user.uid)

        passwordRef.update("password", newPassword)
            .addOnSuccessListener { Log.d(TAG, "Password successfully updated in Firestore!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error updating password in Firestore", e) }
    }

    fun deleteUser() {
        val user = auth.currentUser

        // Elimina el usuario en Firebase Auth
        user?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "User account deleted from Firebase Auth.")

                // Si la eliminación en Firebase Auth fue exitosa, elimina el usuario en Firestore
                deleteUserInFirestore(user)
            }
        }
    }

    private fun deleteUserInFirestore(user: FirebaseUser) {
        val userRef = db.collection("users").document(user.uid)

        userRef.delete()
            .addOnSuccessListener { Log.d(TAG, "User successfully deleted from Firestore!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error deleting user in Firestore", e) }
    }

    // Método para enviar mensajes al Fragmento
    private fun showMessage(message: String) {
        _messageLiveData.value = message
    }

    fun changeThemeToDark() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }

    fun changeThemeToLight() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}
