package com.institutvidreres.winhabit.ui.recompensas

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
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
import kotlinx.coroutines.tasks.await


class RecompensasAdapter(
    private var recompensasList: List<Recompensa>,
    private val context: Context,
    private val viewModel: RecompenasViewModel,
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : RecyclerView.Adapter<RecompensasAdapter.RecompensaViewHolder>() {

    class RecompensaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagenImageView: ImageView = itemView.findViewById(R.id.imagen_recompensa)
        val descripcionTextView: TextView = itemView.findViewById(R.id.descripcion_recompensa)
        val precioTextView: TextView = itemView.findViewById(R.id.precio_recompensa)
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
        holder.precioTextView.text = "Precio: " + recompensa.precio.toString()

        val isConnectedToFirebase = AppUtils.isInternetConnected(context)

        if (isConnectedToFirebase) {
            holder.progressBar.visibility = View.GONE

            val currentUserID = FirebaseAuth.getInstance().currentUser?.uid
            if (currentUserID != null) {
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        // Obtener las monedas del usuario desde Firebase
                        val monedasSnapshot = db.collection("profiles").document(currentUserID).get().await()
                        val monedasUsuario = monedasSnapshot.getLong("monedas") ?: 0L

                        val objetoComprado = viewModel.verificarObjetoComprado(currentUserID, recompensa.firebaseId)
                        if (objetoComprado) {
                            holder.botonRecompensa.visibility = View.GONE
                            holder.imagenMoneda.visibility = View.GONE
                            animateCompra(holder.precioTextView)
                        } else {
                            holder.botonRecompensa.visibility = View.VISIBLE
                            holder.imagenMoneda.visibility = View.VISIBLE
                            holder.botonRecompensa.setOnClickListener {
                                mostrarDialogoCompra(recompensa, currentUserID, holder.itemView, holder.botonRecompensa, monedasUsuario)
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error al obtener las monedas del usuario", e)
                        // Manejar el error aquí
                    }
                }
            }
        } else {
            holder.botonRecompensa.visibility = View.GONE
            holder.imagenMoneda.visibility = View.GONE
            holder.progressBar.visibility = View.VISIBLE
            holder.precioTextView.text = "Cargando..."
        }
    }

    override fun getItemCount(): Int {
        return recompensasList.size
    }

    private fun mostrarDialogoCompra(
        recompensa: Recompensa,
        userId: String,
        itemView: View,
        botonRecompensa: Button,
        monedasUsuario: Long
    ) {
        if (monedasUsuario >= recompensa.precio) {
            // Iniciar la animación
            val anim = AnimationUtils.loadAnimation(context, R.anim.animation_compra)
            botonRecompensa.startAnimation(anim)
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
                            Toast.makeText(context, "¡Recompensa comprada!", Toast.LENGTH_SHORT)
                                .show()
                            notifyDataSetChanged()
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error adding document", e)
                        }
                }
                restarMonedasDespuesDeCompra(userId, recompensa)
                itemView.startAnimation(anim)
                viewModel.newRecompensa(
                    context,
                    recompensa.nombre,
                    recompensa.firebaseId,
                    recompensa.imagenResId,
                    recompensa.descripcion,
                    recompensa.precio,
                    userId
                )
            }
            builder.setNegativeButton("CANCELAR") { _, _ -> }
            builder.show()
        }
        else {
            Toast.makeText(context, "Monedas Insuficientes", Toast.LENGTH_SHORT).show()
        }
    }

    private fun restarMonedasDespuesDeCompra(userId: String, recompensa: Recompensa) {
        // Restar el precio de la recompensa de las monedas del usuario en Firestore
        db.collection("profiles").document(userId)
            .update("monedas", FieldValue.increment(-recompensa.precio.toLong()))
            .addOnFailureListener { e ->
                Log.w(TAG, "Error updating monedas", e)
            }
    }
    private fun incrementarVidas(userId: String, recompensa: Recompensa) {
        val vidasPerdidas = when (recompensa.descripcion) {
            "1 vida" -> 1L
            "3 vidas" -> 3L
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

    private fun animateCompra(view: TextView) {
        val initialX = -view.width.toFloat() // Posición inicial fuera de la pantalla
        view.translationX = initialX // Configurar la posición inicial

        // Animación de translación hacia la derecha
        val translateIn = ObjectAnimator.ofFloat(view, "translationX", initialX, 0f)
        translateIn.duration = 500

        // Animación de desvanecimiento
        val fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)
        fadeIn.duration = 500

        val set = AnimatorSet()
        set.playTogether(translateIn, fadeIn) // Ejecutar ambas animaciones simultáneamente
        set.start()
        view.text = "¡Comprado!"
    }
}
