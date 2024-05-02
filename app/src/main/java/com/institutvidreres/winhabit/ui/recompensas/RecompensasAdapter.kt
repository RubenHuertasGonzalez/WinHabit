package com.institutvidreres.winhabit.ui.recompensas

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.institutvidreres.winhabit.R
import com.institutvidreres.winhabit.utils.AppUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecompensasAdapter(
    private var recompensasList: List<Recompensa>,
    private val context: Context,
    private val viewModel: RecompenasViewModel,
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : RecyclerView.Adapter<RecompensasAdapter.RecompensaViewHolder>() {

    class RecompensaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagenImageView: ImageView = itemView.findViewById(R.id.imagen_recompensa)
        val descripcionTextView: TextView = itemView.findViewById(R.id.descripcion_recompensa)
        val botonRecompensa: Button = itemView.findViewById(R.id.boton_recompensa)
        val imagenMoneda: ImageView = itemView.findViewById(R.id.imagen_moneda)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progress_bar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecompensaViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recompensa, parent, false)
        return RecompensaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecompensaViewHolder, position: Int) {
        val recompensa = recompensasList[position]
        holder.imagenImageView.setImageResource(recompensa.imagenResId)
        holder.descripcionTextView.text = recompensa.descripcion

        val isConnectedToFirebase = AppUtils.isInternetConnected(context)

        if (isConnectedToFirebase) {
            holder.progressBar.visibility = View.GONE

            if (viewModel.esRecompensaVida(recompensa)) {
                holder.botonRecompensa.text = "${recompensa.descripcion} (${recompensa.precio})"
            } else {
                holder.botonRecompensa.text = "${recompensa.precio}"
            }

            val currentUserID = FirebaseAuth.getInstance().currentUser?.uid
            if (currentUserID != null) {
                CoroutineScope(Dispatchers.Main).launch {
                    val objetoComprado = viewModel.verificarObjetoComprado(currentUserID, recompensa.firebaseId)
                    if (objetoComprado) {
                        holder.botonRecompensa.visibility = View.GONE
                        holder.imagenMoneda.visibility = View.GONE
                    } else {
                        holder.botonRecompensa.visibility = View.VISIBLE
                        holder.imagenMoneda.visibility = View.VISIBLE
                        holder.botonRecompensa.setOnClickListener {
                            mostrarDialogoCompra(recompensa, currentUserID)
                        }
                    }
                }
            }
        } else {
            holder.botonRecompensa.visibility = View.GONE
            holder.imagenMoneda.visibility = View.GONE
            holder.progressBar.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return recompensasList.size
    }

    private fun mostrarDialogoCompra(recompensa: Recompensa, userId: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Confirmar Compra")
        builder.setMessage("¿Te gustaría comprar '${recompensa.descripcion}' por ${recompensa.precio} monedas?")
        builder.setPositiveButton("CONFIRMAR") { _, _ ->
            // No actualices la base de datos para marcar la recompensa como comprada
            if (viewModel.esRecompensaVida(recompensa)) {
                // Verificar si la recompensa es de tipo "Vidas" y restar las vidas correspondientes
                incrementarVidas(userId, recompensa)
            } else {
                db.collection("users").document(userId)
                    .update("objetosComprados", FieldValue.arrayUnion(recompensa.firebaseId))
                    .addOnSuccessListener {
                        Toast.makeText(context, "¡Recompensa comprada!", Toast.LENGTH_SHORT).show()
                        notifyDataSetChanged()
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error adding document", e)
                    }
            }
            viewModel.newRecompensa(context, recompensa.nombre, recompensa.firebaseId, recompensa.imagenResId, recompensa.descripcion, recompensa.precio, userId)
        }
        builder.setNegativeButton("CANCELAR") { _, _ -> }
        builder.show()
    }

    private fun incrementarVidas(userId: String, recompensa: Recompensa) {
        val vidasPerdidas = when (recompensa.descripcion) {
            "3 vidas" -> 3L
            "10 vidas" -> 10L
            else -> 0L
        }
        // Obtener las vidas actuales del usuario
        db.collection("profiles").document(userId)
            .get()
            .addOnSuccessListener { document ->
                val vidasActuales = document.getLong("vidasPerdidas") ?: 0L
                val nuevasVidas = if (vidasActuales - vidasPerdidas < 0) 0 else vidasActuales - vidasPerdidas
                // Actualizar las vidas perdidas en la base de datos
                db.collection("profiles").document(userId)
                    .update("vidasPerdidas", nuevasVidas)
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error updating vidasPerdidas", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error getting document", e)
            }
    }
}
