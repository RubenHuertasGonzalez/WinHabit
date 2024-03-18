package com.institutvidreres.winhabit.ui.config

import android.content.ContentValues.TAG
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class ConfViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun changePassword(newPassword: String) {
        val user = auth.currentUser

        // Actualiza la contraseña en Firebase Auth
        user?.updatePassword(newPassword)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "User password updated in Firebase Auth.")

                // Si la actualización en Firebase Auth fue exitosa, actualiza la contraseña en Firestore
                updatePasswordInFirestore(user, newPassword)
            }
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

    fun changeThemeToDark() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }

    fun changeThemeToLight() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}
