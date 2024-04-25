package com.institutvidreres.winhabit.ui.recompensas

import android.annotation.SuppressLint
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

    @SuppressLint("SuspiciousIndentation")
    // En el adaptador
    override fun onBindViewHolder(holder: RecompensaViewHolder, position: Int) {
        val recompensa = recompensasList[position]
        holder.imagenImageView.setImageResource(recompensa.imagenResId)
        holder.descripcionTextView.text = recompensa.descripcion

        val isConnectedToFirebase = AppUtils.isInternetConnected(context)

        if (isConnectedToFirebase) {
            holder.progressBar.visibility = View.GONE

            if (viewModel.esRecompensaVida(recompensa)) {
                // Si es una vida, mostramos la cantidad de vidas como texto en el botón
                holder.botonRecompensa.text = "${recompensa.descripcion} (${recompensa.precio})"
            } else {
                // Si no es una vida, mostramos el precio normal
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
                            mostrarDialogoCompra(recompensa)
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

    private fun mostrarDialogoCompra(recompensa: Recompensa) {
        val currentUserID = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserID != null) {
            CoroutineScope(Dispatchers.Main).launch {
                val objetoComprado = viewModel.verificarObjetoComprado(currentUserID, recompensa.firebaseId)
                if (objetoComprado) {
                    Toast.makeText(context, "¡Recompensa ya comprada!", Toast.LENGTH_SHORT).show()
                } else {
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("Confirmar Compra")
                    builder.setMessage("¿Te gustaría comprar '${recompensa.descripcion}' por ${recompensa.precio} monedas?")
                    builder.setPositiveButton("CONFIRMAR") { _, _ ->
                        db.collection("users").document(currentUserID)
                            .update("objetosComprados", FieldValue.arrayUnion(recompensa.firebaseId))
                            .addOnSuccessListener {
                                Toast.makeText(context, "¡Recompensa comprada!", Toast.LENGTH_SHORT).show()
                                notifyDataSetChanged()
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error adding document", e)
                            }
                        viewModel.newRecompensa(context, recompensa.nombre, recompensa.firebaseId, recompensa.imagenResId, recompensa.descripcion, recompensa.precio)
                    }
                    builder.setNegativeButton("CANCELAR") { _, _ -> }
                    builder.show()
                }
            }
        }
    }
}
